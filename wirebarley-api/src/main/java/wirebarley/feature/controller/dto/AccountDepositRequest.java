package wirebarley.feature.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record AccountDepositRequest(
    @NotNull @NotBlank(message = "계좌 아이디가 비어 있습니다.") Long accountId,
    @NotNull @NotBlank(message = "이체 금액이 비어 있습니다.") int transferAmount,
    @NotNull @NotBlank(message = "이체 내용이 비어 있습니다.") String description
) {
}
