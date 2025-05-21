package wirebarley.feature.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.service.WirebarleyService;

@Validated
@RestController
@RequestMapping("/api/v1/wirebarley/accounts")
@RequiredArgsConstructor
public class WirebarleyController {
    private final WirebarleyService wirebarleyService;

    @PostMapping
    public void createAccount(
        @RequestBody @Validated AccountCreateRequest request
    ) {
        wirebarleyService.createAccount(request);
    }

    @DeleteMapping("/{account_id}")
    public void deleteAccount(
        @PathVariable long account_id
    ) {

    }

    @PostMapping("/{account_id}/deposit")
    public void deposit(
        @PathVariable long account_id
    ) {

    }

    @PostMapping("/{account_id}/withdraw")
    public void withdraw(
        @PathVariable long account_id
    ) {

    }

    @PostMapping("/transfer")
    public void transfer() {

    }

    @GetMapping("/{account_id}/transaction")
    public void getTransaction(
        @PathVariable long account_id
    ) {

    }
}
