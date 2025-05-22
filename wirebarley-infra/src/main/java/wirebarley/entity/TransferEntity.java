package wirebarley.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wirebarley.AbstractEntity;
import wirebarley.domain.Transfer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderAccountId;
    private Long receiverAccountId;
    private int transferAmount;
    private String description;
    private LocalDateTime transferAt;

    public TransferEntity from(
        Transfer transfer
    ) {
        return new TransferEntity(
            transfer.getId(),
            transfer.getSenderAccountId(),
            transfer.getReceiverAccountId(),
            transfer.getTransferAmount(),
            transfer.getDescription(),
            transfer.getTransferAt()
        );
    }

    public Transfer toModel() {
        return new Transfer(
            this.senderAccountId,
            this.receiverAccountId,
            this.transferAmount,
            this.description,
            this.transferAt
        );
    }
}
