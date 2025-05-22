package wirebarley.feature.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record AccountWithdrawRequest(
    @NotNull @NotBlank(message = "계좌 아이디가 비어 있습니다.") Long accountId,
    @NotNull @NotBlank(message = "출금 금액이 비어 있습니다.") int transferAmount,
    String description
) {
}
