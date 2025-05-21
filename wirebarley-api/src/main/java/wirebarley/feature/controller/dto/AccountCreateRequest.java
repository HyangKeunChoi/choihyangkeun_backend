package wirebarley.feature.controller.dto;

import javax.validation.constraints.NotBlank;

public record AccountCreateRequest(
    @NotBlank(message = "유저 아이디가 비어 있습니다.") Long userId,
    @NotBlank(message = "계좌 번호가 비어 있습니다.") String accountNumber
) {
}
