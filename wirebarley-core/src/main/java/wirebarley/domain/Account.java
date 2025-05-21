package wirebarley.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Account {
    Long id;
    String userId;
    String accountNumber;
    BigDecimal balance;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Builder
    public Account(
        Long id,
        String userId,
        String accountNumber,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
