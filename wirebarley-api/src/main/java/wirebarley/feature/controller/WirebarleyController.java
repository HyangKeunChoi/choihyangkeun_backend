package wirebarley.feature.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.controller.dto.AccountDepositRequest;
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

        // 200 ok,  201 created로 응답 가능
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{account_id}")
    public ResponseEntity deleteAccount(
        @PathVariable(name = "account_id") Long accountId
    ) {
        wirebarleyService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    // 입금
    @PostMapping("/deposit")
    public ResponseEntity deposit(
        @RequestBody @Validated AccountDepositRequest request
    ) {
        wirebarleyService.depositAccountWithLock(request);
        return ResponseEntity.ok().build();
    }

    // 출금
    @PostMapping("/{account_id}/withdraw")
    public void withdraw(
        @PathVariable(name = "account_id") Long accountId
    ) {

    }

    // 이체
    @PostMapping("/transfer")
    public void transfer() {

    }

    // 거래 내역 조회
    @GetMapping("/{account_id}/transaction")
    public void getTransaction(
        @PathVariable(name = "account_id") Long accountId
    ) {

    }
}
