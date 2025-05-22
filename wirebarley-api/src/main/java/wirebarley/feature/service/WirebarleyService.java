package wirebarley.feature.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.exception.AccountNotExistException;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.controller.dto.AccountDepositRequest;
import wirebarley.repository.IAccountRepository;
import wirebarley.util.RedisLockUtils;

@Transactional
@Service
@RequiredArgsConstructor
public class WirebarleyService {
    private final IAccountRepository accountRepository;
    private final RedisLockUtils redisLockUtils;
    private static final String DEPOSIT_LOCK_PREFIX = "deposit:lock:account:";

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
        Account account = accountRepository.findByAccountId(accountId);
        if (account == null) throw new AccountNotExistException();

        accountRepository.deleteByAccountId(account.getId());
    }

    // redis lock의 범위가 deposit 트랜잭션 보다 바깥으로 설정
    // 입금액의 동시성 보장을 위해 redis lock(spin lock 방식) 이용
    // TODO : 입금이 실패하면 에러가 발생하지 않고 별도의 보상 트랜잭션이 발생할 수 있도록 변경 고려
    public void depositAccountWithLock(AccountDepositRequest request) {
        String lockKey = DEPOSIT_LOCK_PREFIX + request.accountId();
        String clientId = redisLockUtils.acquireLockWithRetry(lockKey);

        if (clientId == null) {
            throw new IllegalStateException("Cannot acquire lock for account: " + request.accountId());
        }

        try {
            depositAccount(request);
        } finally {
            redisLockUtils.releaseLock(lockKey, clientId);
        }
    }

    @Transactional
    public void depositAccount(AccountDepositRequest request) {
        Account account = accountRepository.findByAccountId(request.accountId());
        if (account == null) throw new AccountNotExistException();

        account.deposit(request.transferAmount());
    }
}
