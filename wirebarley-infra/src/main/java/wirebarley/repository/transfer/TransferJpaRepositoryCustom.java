package wirebarley.repository.transfer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import wirebarley.domain.Transfer;

public interface TransferJpaRepositoryCustom {
    Slice<Transfer> findAllByAccountId(Long accountId, Pageable pageable);
}
