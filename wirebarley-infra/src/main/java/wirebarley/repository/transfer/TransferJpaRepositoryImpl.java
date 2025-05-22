package wirebarley.repository.transfer;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import wirebarley.domain.Transfer;
import wirebarley.entity.TransferEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static wirebarley.entity.QTransferEntity.transferEntity;

@Repository
public class TransferJpaRepositoryImpl extends QuerydslRepositorySupport implements TransferJpaRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TransferJpaRepositoryImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(Transfer.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public Slice<Transfer> findAllByAccountId(Long accountId, Pageable pageable) {
        List<TransferEntity> transferEntities = jpaQueryFactory.selectFrom(transferEntity)
            .where(transferEntity.senderAccountId.eq(accountId).or(transferEntity.receiverAccountId.eq(accountId)))
            .orderBy(transferEntity.transferAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<Transfer> results = transferEntities.stream()
            .map(TransferEntity::toModel)
            .collect(Collectors.toList());

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
