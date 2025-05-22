package wirebarley.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Account {
    Long id;
    Long userId;
    String accountNumber;
    BigDecimal balance;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Builder
    public Account(
        Long userId,
        String accountNumber,
        BigDecimal balance,
        AccountStatus status
    ) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }

    public void deposit(int amount) {
        this.balance = this.balance.add(BigDecimal.valueOf(amount));
    }

    public void withdraw(int amount) {
        this.balance = this.balance.subtract(BigDecimal.valueOf(amount));
    }
}
