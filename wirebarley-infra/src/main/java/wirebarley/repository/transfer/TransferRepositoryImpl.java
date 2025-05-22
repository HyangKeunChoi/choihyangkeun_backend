package wirebarley.repository.transfer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import wirebarley.domain.Transfer;
import wirebarley.entity.TransferEntity;
import wirebarley.repository.ITransfersRepository;

@AllArgsConstructor
@Repository
public class TransferRepositoryImpl implements ITransfersRepository {
    private final TransferJpaRepository transferJpaRepository;

    @Override
    public Transfer save(Transfer transfer) {
        return transferJpaRepository.save(new TransferEntity().from(transfer)).toModel();
    }

    @Override
    public Slice<Transfer> findAllByAccountId(Long accountId, Pageable pageable) {
        return transferJpaRepository.findAllByAccountId(
            accountId, pageable
        );
    }
}
