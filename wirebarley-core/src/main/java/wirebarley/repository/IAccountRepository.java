package wirebarley.repository;

import wirebarley.domain.Account;

public interface IAccountRepository {
    Account save(Account account);
    Account findById(Long accountId);
    void deleteByAccountId(Long accountId);
}
