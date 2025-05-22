package wirebarley.domain;

import org.junit.jupiter.api.Test;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    void 계좌_생성_확인() {
        // Given
        Long userId = 1L;
        String accountNumber = "123-456-789";
        BigDecimal initialBalance = BigDecimal.valueOf(10000);
        AccountStatus status = AccountStatus.ACTIVE;

        // When
        Account account = Account.builder()
            .userId(userId)
            .accountNumber(accountNumber)
            .balance(initialBalance)
            .status(status)
            .build();

        // Then
        assertNotNull(account);
        assertEquals(userId, account.getUserId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(initialBalance, account.getBalance());
        assertEquals(status, account.getStatus());
        assertNull(account.getId());
        assertNull(account.getCreatedAt());
        assertNull(account.getUpdatedAt());
    }

    @Test
    void 입금_성공() {
        // Given
        Account account = Account.builder()
            .userId(1L)
            .accountNumber("123-456-789")
            .balance(BigDecimal.valueOf(10000))
            .status(AccountStatus.ACTIVE)
            .build();
        int depositAmount = 5000;
        BigDecimal expectedBalance = BigDecimal.valueOf(15000);

        // When
        account.deposit(depositAmount);

        // Then
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    void 출금_성공() throws InsufficientResourcesException {
        // Given
        Account account = Account.builder()
            .userId(1L)
            .accountNumber("123-456-789")
            .balance(BigDecimal.valueOf(10000))
            .status(AccountStatus.ACTIVE)
            .build();
        int withdrawAmount = 3000;
        BigDecimal expectedBalance = BigDecimal.valueOf(7000);

        // When
        account.withdraw(withdrawAmount);

        // Then
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    void 출금_실패_잔액_부족() {
        // Given
        Account account = Account.builder()
            .userId(1L)
            .accountNumber("123-456-789")
            .balance(BigDecimal.valueOf(1000))
            .status(AccountStatus.ACTIVE)
            .build();
        int withdrawAmount = 2000;

        // When
        assertThrows(InsufficientResourcesException.class, () -> {
            account.withdraw(withdrawAmount);
        });

        // Then
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }
}
