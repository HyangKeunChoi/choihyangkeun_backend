package wirebarley.repository.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wirebarley.domain.Account;
import wirebarley.entity.AccountEntity;
import wirebarley.repository.IAccountRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class AccountRepositoryImpl implements IAccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Account save(Account account) {
        return accountJpaRepository.save(new AccountEntity().from(account)).toModel();
    }

    @Override
    public Account findById(Long accountId) {
        Optional<AccountEntity> account = accountJpaRepository.findById(accountId);
        if (account.isPresent()) {
            return account.get().toModel();
        } else {
            return null;
        }
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        accountJpaRepository.deleteById(accountId);
    }
}
