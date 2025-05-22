package com.example.wirebarleyapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wirebarley.domain.Account;
import wirebarley.domain.AccountStatus;
import wirebarley.domain.Transfer;
import wirebarley.exception.AccountNotExistException;
import wirebarley.exception.InsufficientException;
import wirebarley.exception.WithdrawLimitException;
import wirebarley.feature.controller.dto.AccountCreateRequest;
import wirebarley.feature.controller.dto.AccountDepositRequest;
import wirebarley.feature.controller.dto.AccountTransferRequest;
import wirebarley.feature.controller.dto.AccountWithdrawRequest;
import wirebarley.feature.service.WirebarleyService;
import wirebarley.repository.IAccountRepository;
import wirebarley.repository.ITransfersRepository;
import wirebarley.util.RedisUtils;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WirebarleyServiceTest {

    @InjectMocks
    private WirebarleyService wirebarleyService;

    @Mock
    private IAccountRepository accountRepository;

    @Mock
    private ITransfersRepository transfersRepository;

    @Mock
    private RedisUtils redisUtils;

    @Test
    void 계좌_등록에_성공_한다() {
        // Given
        AccountCreateRequest request = new AccountCreateRequest(1L, "123-456-789");
        Account savedAccount = Account.builder()
            .userId(request.userId())
            .accountNumber(request.accountNumber())
            .status(AccountStatus.ACTIVE)
            .balance(BigDecimal.ZERO)
            .build();
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // When
        wirebarleyService.createAccount(request);

        // Then
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void 계좌_삭제에_성공_한다() {
        // Given
        Long accountIdToDelete = 10L;
        Account existingAccount = Account.builder().build();
        existingAccount.setId(10L);
        when(accountRepository.findById(accountIdToDelete)).thenReturn(existingAccount);
        doNothing().when(accountRepository).deleteByAccountId(accountIdToDelete);

        // When
        wirebarleyService.deleteAccount(accountIdToDelete);

        // Then
        verify(accountRepository, times(1)).findById(accountIdToDelete);
        verify(accountRepository, times(1)).deleteByAccountId(accountIdToDelete);
    }

    @Test
    void 존재하지_않는계좌는_계좌없음_오류가_발생한다() {
        // Given
        Long nonExistingAccountId = 99L;
        when(accountRepository.findById(nonExistingAccountId)).thenReturn(null);

        // When
        assertThrows(AccountNotExistException.class, () -> wirebarleyService.deleteAccount(nonExistingAccountId));

        // Then
        verify(accountRepository, times(1)).findById(nonExistingAccountId);
        verify(accountRepository, never()).deleteByAccountId(anyLong());
    }

    @Test
    void 계좌_입금에_성공_한다() {
        // Given
        AccountDepositRequest request = new AccountDepositRequest(10L, 5000, "입금");
        Account existingAccount = Account.builder().balance(BigDecimal.valueOf(10000)).build();
        when(accountRepository.findById(10L)).thenReturn(existingAccount);
        when(transfersRepository.save(any(Transfer.class))).thenReturn(any(Transfer.class));

        // When
        wirebarleyService.depositAccount(request);

        // Then
        assertEquals(BigDecimal.valueOf(15000), existingAccount.getBalance());
        verify(accountRepository, times(1)).findById(10L);
        verify(transfersRepository, times(1)).save(any(Transfer.class));
    }

    @Test
    void 계좌가_존재하지_않는다면_입금에_실패한다() {
        // Given
        AccountDepositRequest request = new AccountDepositRequest(99L, 5000, "입금");
        when(accountRepository.findById(99L)).thenReturn(null);

        // When
        assertThrows(AccountNotExistException.class, () -> wirebarleyService.depositAccount(request));

        // Then
        verify(accountRepository, times(1)).findById(99L);
        verify(transfersRepository, never()).save(any(Transfer.class));
    }

    @Test
    void 계좌_출금에_성공한다() {
        // Given
        AccountWithdrawRequest request = new AccountWithdrawRequest(10L, 3000, "출금");
        Account existingAccount = Account.builder().balance(BigDecimal.valueOf(10000)).build();
        when(accountRepository.findById(10L)).thenReturn(existingAccount);
        when(redisUtils.get(anyString())).thenReturn("0");
        when(redisUtils.incrementOrResetCounter(anyString(), any(Duration.class), anyInt())).thenReturn(3000L);
        when(transfersRepository.save(any(Transfer.class))).thenReturn(any(Transfer.class));

        // When
        wirebarleyService.withdraw(request);

        // Then
        assertEquals(BigDecimal.valueOf(7000), existingAccount.getBalance());
        verify(accountRepository, times(1)).findById(10L);
        verify(redisUtils, times(1)).get(anyString());
        verify(redisUtils, times(1)).incrementOrResetCounter(anyString(), any(Duration.class), anyInt());
        verify(transfersRepository, times(1)).save(any(Transfer.class));
    }

    @Test
    void 계좌가_존재하지_않으면_출금에_실패한다() {
        // Given
        AccountWithdrawRequest request = new AccountWithdrawRequest(99L, 3000, "출금");
        when(accountRepository.findById(99L)).thenReturn(null);

        // When
        assertThrows(AccountNotExistException.class, () -> wirebarleyService.withdraw(request));

        // Then
        verify(accountRepository, times(1)).findById(99L);
        verify(redisUtils, never()).get(anyString());
        verify(redisUtils, never()).incrementOrResetCounter(anyString(), any(Duration.class), anyInt());
        verify(transfersRepository, never()).save(any(Transfer.class));
    }

    @Test
    void 당일한도_1_000_000_이_넘으면_한도_초과가_발생한다() {
        // Given
        AccountWithdrawRequest request = new AccountWithdrawRequest(10L, 3000, "출금");
        Account existingAccount = Account.builder().balance(BigDecimal.valueOf(10000)).build();
        when(accountRepository.findById(10L)).thenReturn(existingAccount);
        when(redisUtils.get(anyString())).thenReturn("1500000"); // 한도 초과 상황

        // When
        assertThrows(WithdrawLimitException.class, () -> wirebarleyService.withdraw(request));

        // Then
        verify(accountRepository, times(1)).findById(10L);
        verify(redisUtils, times(1)).get(anyString());
        verify(redisUtils, never()).incrementOrResetCounter(anyString(), any(Duration.class), anyInt());
        verify(transfersRepository, never()).save(any(Transfer.class));
    }

    @Test
    void 출금시_잔액이_부족하면_실패_오류가_발생한다() {
        // Given
        AccountWithdrawRequest request = new AccountWithdrawRequest(10L, 15000, "출금");
        Account existingAccount = Account.builder().balance(BigDecimal.valueOf(10000)).build();
        when(accountRepository.findById(10L)).thenReturn(existingAccount);
        when(redisUtils.get(anyString())).thenReturn("0");

        // When
        assertThrows(InsufficientException.class, () -> wirebarleyService.withdraw(request));

        // Then
        verify(accountRepository, times(1)).findById(10L);
        verify(redisUtils, times(1)).get(anyString());
        verify(redisUtils, never()).incrementOrResetCounter(anyString(), any(Duration.class), anyInt());
        verify(transfersRepository, never()).save(any(Transfer.class));
    }

    @Test
    void 계좌_이체에_성공() {
        // Given
        AccountTransferRequest request = new AccountTransferRequest(1L, 2L, 2000, "이체");
        Account sender = Account.builder().balance(BigDecimal.valueOf(10000)).build();
        Account receiver = Account.builder().balance(BigDecimal.valueOf(5000)).build();
        when(accountRepository.findById(1L)).thenReturn(sender);
        when(accountRepository.findById(2L)).thenReturn(receiver);
        when(transfersRepository.save(any(Transfer.class))).thenReturn(any(Transfer.class));

        // When
        wirebarleyService.transfer(request);

        // Then
        assertEquals(BigDecimal.valueOf(8000), sender.getBalance());
        assertEquals(BigDecimal.valueOf(7000), receiver.getBalance());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
        verify(transfersRepository, times(2)).save(any(Transfer.class));
    }
}