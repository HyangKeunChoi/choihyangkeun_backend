package wirebarley.feature.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record AccountTransferRequest(
    @NotNull @NotBlank(message = "송금 계좌 아이디가 비어 있습니다.") Long senderAccountId,
    @NotNull @NotBlank(message = "이체 받을 계좌의 아이디가 비어 있습니다.") Long receiverAccountId,
    @NotNull @NotBlank(message = "이체 금액이 비어 있습니다.") @Positive(message = "0보다 커야 합니다.") int transferAmount,
    String description
) {
}
