package wirebarley.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import wirebarley.entity.AccountEntity;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
}
