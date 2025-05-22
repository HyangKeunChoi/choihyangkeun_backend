package wirebarley.repository.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import wirebarley.entity.TransferEntity;

public interface TransferJpaRepository extends JpaRepository<TransferEntity, Long>, TransferJpaRepositoryCustom {

}
