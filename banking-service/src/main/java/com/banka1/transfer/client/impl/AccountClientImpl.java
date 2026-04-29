package com.banka1.transfer.client.impl;

import com.banka1.account_service.dto.response.InternalAccountDetailsDto;
import com.banka1.transfer.client.AccountClient;
import com.banka1.transfer.dto.client.AccountDto;
import com.banka1.transfer.dto.client.PaymentDto;
import com.banka1.transfer.dto.client.UpdatedBalanceResponseDto;
import com.banka1.transfer.exception.BusinessException;
import com.banka1.transfer.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementacija klijenta za Account Service koja koristi direktne pozive (inner call, bez HTTP).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountClientImpl implements AccountClient {

    private final com.banka1.account_service.service.AccountService internalAccountService;

    @Override
    public AccountDto getAccountDetails(String accountNumber) {
        try {
            InternalAccountDetailsDto r = internalAccountService.getAccountDetails(accountNumber);
            return new AccountDto(
                    r.accountNumber(),
                    r.ownerId(),
                    r.currency(),
                    r.availableBalance(),
                    r.status(),
                    r.accountType()
            );
        } catch (com.banka1.account_service.exception.BusinessException e) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Račun " + accountNumber + " ne postoji.");
        } catch (Exception e) {
            log.error("Account service error for account {}: {}", accountNumber, e.getMessage());
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Račun " + accountNumber + " nije pronađen.");
        }
    }

    @Override
    public UpdatedBalanceResponseDto executeTransfer(PaymentDto p) {
        try {
            com.banka1.account_service.dto.response.UpdatedBalanceResponseDto r =
                    internalAccountService.transfer(new com.banka1.account_service.dto.request.PaymentDto(
                            p.fromAccountNumber(), p.toAccountNumber(),
                            p.fromAmount(), p.toAmount(), p.commission(), p.clientId()
                    ));
            return new UpdatedBalanceResponseDto(r.getSenderBalance(), r.getReceiverBalance());
        } catch (com.banka1.account_service.exception.BusinessException e) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_FUNDS, "Neuspešan transfer: " + e.getMessage());
        } catch (Exception e) {
            log.error("Critical error during account transfer: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TRANSFER_NOT_FOUND, "Greška prilikom izvršavanja transfera.");
        }
    }
}
