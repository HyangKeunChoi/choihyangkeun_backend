package wirebarley.feature.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.repository.IAccountRepository;

@Transactional
@Service
public class WirebarleyService  {
    private final IAccountRepository accountRepository;

    public void createAccount(AccountCreateRequest request) {
        accountRepository.save();
    }
}
