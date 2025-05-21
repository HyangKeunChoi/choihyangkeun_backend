package wirebarley.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Transfer {
    Long id;
    Long senderAccountId;
    Long receiverAccountId;
    int transferAmount;
    String description;
    LocalDateTime transferDate;
    LocalDateTime createDate;
    LocalDateTime updateDate;

    @Builder
    public Transfer(
        Long id,
        Long senderAccountId,
        Long receiverAccountId,
        int transferAmount,
        String description,
        LocalDateTime transferDate,
        LocalDateTime createDate,
        LocalDateTime updateDate
    ) {
        this.id = id;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.transferAmount = transferAmount;
        this.description = description;
        this.transferDate = transferDate;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
