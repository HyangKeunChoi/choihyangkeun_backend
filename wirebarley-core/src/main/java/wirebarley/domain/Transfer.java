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
    LocalDateTime transferAt;
    LocalDateTime createDate;
    LocalDateTime updateDate;

    @Builder
    public Transfer(
        Long senderAccountId,
        Long receiverAccountId,
        int transferAmount,
        String description,
        LocalDateTime transferAt
    ) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.transferAmount = transferAmount;
        this.description = description;
        this.transferAt = transferAt;
    }
}
