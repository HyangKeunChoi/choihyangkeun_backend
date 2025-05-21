package wirebarley.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import wirebarley.entity.AccountEntity;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountId(Long accountId);
}
