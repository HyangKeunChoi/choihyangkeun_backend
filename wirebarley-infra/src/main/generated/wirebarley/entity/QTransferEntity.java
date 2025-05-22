package wirebarley.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTransferEntity is a Querydsl query type for TransferEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransferEntity extends EntityPathBase<TransferEntity> {

    private static final long serialVersionUID = 1407545917L;

    public static final QTransferEntity transferEntity = new QTransferEntity("transferEntity");

    public final wirebarley.QAbstractEntity _super = new wirebarley.QAbstractEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> receiverAccountId = createNumber("receiverAccountId", Long.class);

    public final NumberPath<Long> senderAccountId = createNumber("senderAccountId", Long.class);

    public final NumberPath<Integer> transferAmount = createNumber("transferAmount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> transferAt = createDateTime("transferAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTransferEntity(String variable) {
        super(TransferEntity.class, forVariable(variable));
    }

    public QTransferEntity(Path<? extends TransferEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransferEntity(PathMetadata metadata) {
        super(TransferEntity.class, metadata);
    }

}

