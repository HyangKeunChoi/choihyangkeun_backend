package wirebarley.feature.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.domain.Transfer;
import wirebarley.exception.AccountNotExistException;
import wirebarley.exception.InsufficientException;
import wirebarley.exception.WithdrawLimitException;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.controller.dto.AccountDepositRequest;
import wirebarley.feature.controller.dto.AccountTransferRequest;
import wirebarley.feature.controller.dto.AccountWithdrawRequest;
import wirebarley.repository.IAccountRepository;
import wirebarley.repository.ITransfersRepository;
import wirebarley.util.RedisUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class WirebarleyService {
    private final IAccountRepository accountRepository;
    private final ITransfersRepository transfersRepository;

    private final RedisUtils redisUtils;
    private static final String DEPOSIT_LOCK_PREFIX = "deposit:lock:account:";
    private static final String WITHDRAW_LOCK_PREFIX = "withdraw:lock:account:";
    private static final String WITHDRAW_TODAY_LOCK_PREFIX = "withdraw_today:lock:account:";
    private static final String TRANSFER_LOCK_PREFIX = "transfer:lock:account:";

    @Transactional
    public void createAccount(AccountCreateRequest request) {
        Account account = Account.builder()
            .userId(request.userId())
            .accountNumber(request.accountNumber())
            .status(AccountStatus.ACTIVE)
            .build();

        // TODO : validation
        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new AccountNotExistException();

        accountRepository.deleteByAccountId(account.getId());
    }

    // redis lock의 범위가 deposit 트랜잭션 보다 바깥으로 설정
    // 입금액의 동시성 보장을 위해 redis lock(spin lock 방식) 이용
    // TODO : 입금이 실패하면 에러가 발생하지 않고 별도의 보상 트랜잭션이 발생할 수 있도록 변경 고려
    public void depositAccountWithLock(AccountDepositRequest request) {
        String lockKey = DEPOSIT_LOCK_PREFIX + request.accountId();
        String clientId = redisUtils.acquireLockWithRetry(lockKey);

        if (clientId == null) {
            throw new IllegalStateException("Cannot acquire lock for account: " + request.accountId());
        }

        try {
            depositAccount(request);
        } finally {
            redisUtils.releaseLock(lockKey, clientId);
        }
    }

    // TODO : depositAccountWithLock과 로직이 거의 비슷하므로 템플릿 메소드 패턴을 고려해 본다.
    public void withdrawAccountWithLock(AccountWithdrawRequest request) {
        String lockKey = WITHDRAW_LOCK_PREFIX + request.accountId();
        String clientId = redisUtils.acquireLockWithRetry(lockKey);

        if (clientId == null) {
            throw new IllegalStateException("Cannot acquire lock for account: " + request.accountId());
        }

        try {
            withdraw(request);
        } finally {
            redisUtils.releaseLock(lockKey, clientId);
        }
    }

    public void transferAccountWithLock(AccountTransferRequest request) {
        String lockKey = TRANSFER_LOCK_PREFIX + request.senderAccountId() + request.receiverAccountId();
        String clientId = redisUtils.acquireLockWithRetry(lockKey);

        if (clientId == null) {
            throw new IllegalStateException("Cannot acquire lock for account: " + request.senderAccountId() + " receiver : " + request.receiverAccountId());
        }

        try {
            transfer(request);
        } finally {
            redisUtils.releaseLock(lockKey, clientId);
        }
    }

    @Transactional
    public void depositAccount(AccountDepositRequest request) {
        Account account = accountRepository.findById(request.accountId());
        if (account == null) throw new AccountNotExistException();

        account.deposit(request.transferAmount());

        Transfer transfer = Transfer.builder()
            .senderAccountId(account.getId())
            .receiverAccountId(account.getId())
            .transferAmount(request.transferAmount())
            .description(request.description())
            .transferAt(LocalDateTime.now())
            .build();
        transfersRepository.save(transfer);
    }

    @Transactional
    public void withdraw(AccountWithdrawRequest request) {
        Account account = accountRepository.findById(request.accountId());
        if (account == null) throw new AccountNotExistException();

        // 레디스로 일일 한도 계산
        // REDIS가 죽으면 db를 조회 할지 고려해 본다.
        String key = WITHDRAW_TODAY_LOCK_PREFIX + LocalDate.now() + ":" + request.accountId();
        int todayTargetAccountTotalWithdraw = Integer.parseInt(redisUtils.get(key));

        if (todayTargetAccountTotalWithdraw > 1_000_000) {
            throw new WithdrawLimitException();
        }

        try {
            account.withdraw(request.transferAmount());
        } catch (IllegalArgumentException e) {
            throw new InsufficientException();
        }
        redisUtils.incrementOrResetCounter(
            key,
            Duration.ofHours(30), // 하루보다 크게 설정
            request.transferAmount()
        );

        Transfer transfer = Transfer.builder()
            .senderAccountId(account.getId())
            .receiverAccountId(account.getId())
            .transferAmount(request.transferAmount() * -1)
            .description(request.description())
            .transferAt(LocalDateTime.now())
            .build();

        transfersRepository.save(transfer);
    }

    @Transactional
    public void transfer(AccountTransferRequest request) {
        Account sender = accountRepository.findById(request.senderAccountId());
        Account receiver = accountRepository.findById(request.receiverAccountId());

        if (sender == null || receiver == null) {
            throw new AccountNotExistException();
        }

        if (sender.getBalance().compareTo(BigDecimal.valueOf(request.transferAmount())) < 0) {
            throw new InsufficientException();
        }

        // 1. Sender 계좌에서 금액 차감
        sender.withdraw(request.transferAmount());

        // 2. Receiver 계좌에 금액 입금
        receiver.deposit(request.transferAmount());

        // 3. 이체 내역 기록 (Sender -> Receiver)
        Transfer senderTransfer = Transfer.builder()
            .senderAccountId(sender.getId())
            .receiverAccountId(receiver.getId())
            .transferAmount(request.transferAmount() * -1) // 보낸 금액은 음수로 기록
            .description(request.description())
            .transferAt(LocalDateTime.now())
            .build();
        transfersRepository.save(senderTransfer);

        // 4. 이체 내역 기록 (Receiver <- Sender)
        Transfer receiverTransfer = Transfer.builder()
            .senderAccountId(sender.getId())
            .receiverAccountId(receiver.getId())
            .transferAmount(request.transferAmount())
            .description(request.description())
            .transferAt(LocalDateTime.now())
            .build();
        transfersRepository.save(receiverTransfer);
    }

    @Transactional(readOnly = true)
    public Slice<Transfer> getTransaction(
        Long accountId,
        Pageable pageable
    ) {
        return transfersRepository.findAllByAccountId(
            accountId, pageable
        );
    }
}
