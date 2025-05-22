package wirebarley.feature.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.domain.Transfer;
import wirebarley.exception.AccountNotExistException;
import wirebarley.exception.WithdrawLimitException;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.controller.dto.AccountDepositRequest;
import wirebarley.feature.controller.dto.AccountWithdrawRequest;
import wirebarley.repository.IAccountRepository;
import wirebarley.repository.ITransfersRepository;
import wirebarley.util.RedisUtils;

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
        String key = WITHDRAW_LOCK_PREFIX + LocalDate.now() + ":" + request.accountId();
        int todayTargetAccountTotalWithdraw = Integer.parseInt(redisUtils.get(key));

        if (todayTargetAccountTotalWithdraw > 1_000_000) {
            throw new WithdrawLimitException();
        }

        account.withdraw(request.transferAmount());
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
}
