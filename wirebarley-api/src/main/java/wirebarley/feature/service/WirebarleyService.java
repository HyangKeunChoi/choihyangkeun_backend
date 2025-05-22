package wirebarley.feature.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.exception.AccountNotExistException;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.repository.IAccountRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class WirebarleyService {
    private final IAccountRepository accountRepository;

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
}
