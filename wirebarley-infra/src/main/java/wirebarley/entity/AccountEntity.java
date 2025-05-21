package wirebarley.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wirebarley.AbstractEntity;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String accountNumber;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public AccountEntity from(
        Account account
    ) {
        return new AccountEntity(
            account.getId(),
            account.getUserId(),
            account.getAccountNumber(),
            account.getBalance(),
            account.getStatus()
        );
    }

    public Account toModel() {
        return new Account(
            this.userId,
            this.accountNumber,
            this.balance,
            this.status
        );
    }
}
