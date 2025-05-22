package wirebarley.repository.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wirebarley.domain.Account;
import wirebarley.entity.AccountEntity;
import wirebarley.repository.IAccountRepository;

@AllArgsConstructor
@Repository
public class AccountRepositoryImpl implements IAccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Account save(Account account) {
        return accountJpaRepository.save(new AccountEntity().from(account)).toModel();
    }

    @Override
    public Account findByAccountId(Long accountId) {
        return accountJpaRepository.findByAccountId(accountId).toModel();
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        accountJpaRepository.deleteById(accountId);
    }
}
