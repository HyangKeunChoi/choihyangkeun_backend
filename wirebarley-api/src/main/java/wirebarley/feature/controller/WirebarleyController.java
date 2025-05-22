package wirebarley.feature.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createAccount(
        @RequestBody @Validated AccountCreateRequest request
    ) {
        wirebarleyService.createAccount(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{account_id}")
    public ResponseEntity deleteAccount(
        @PathVariable(name = "account_id") Long accountId
    ) {
        wirebarleyService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{account_id}/deposit")
    public ResponseEntity deposit(
        @PathVariable(name = "account_id") Long accountId
    ) {

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{account_id}/withdraw")
    public void withdraw(
        @PathVariable(name = "account_id") Long accountId
    ) {

    }

    @PostMapping("/transfer")
    public void transfer() {

    }

    @GetMapping("/{account_id}/transaction")
    public void getTransaction(
        @PathVariable(name = "account_id") Long accountId
    ) {

    }
}
