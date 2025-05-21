package wirebarley.feature.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.repository.IAccountRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class WirebarleyService  {
    private final IAccountRepository accountRepository;

    public void createAccount(AccountCreateRequest request) {
        Account account = Account.builder()
            .userId(request.userId())
            .accountNumber(request.accountNumber())
            .status(AccountStatus.ACTIVE)
            .build();


        accountRepository.save(account);
    }
}
