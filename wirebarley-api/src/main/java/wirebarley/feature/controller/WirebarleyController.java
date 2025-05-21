package wirebarley.feature.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.service.WirebarleyService;

@Validated
@RestController
@RequestMapping("/api/v1/wirebarley")
@RequiredArgsConstructor
public class WirebarleyController {
    private final WirebarleyService wirebarleyService;

    @PostMapping("/accounts")
    public void createAccount(
        @RequestBody @Validated AccountCreateRequest request
    ) {
        wirebarleyService.createAccount(request);
    }
}
