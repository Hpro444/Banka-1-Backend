package com.banka1.credit_service.rest_client;

import com.banka1.account_service.dto.response.InternalAccountDetailsDto;
import com.banka1.credit_service.domain.enums.CurrencyCode;
import com.banka1.credit_service.dto.request.BankPaymentDto;
import com.banka1.credit_service.dto.request.PaymentDto;
import com.banka1.credit_service.dto.response.AccountDetailsResponseDto;
import com.banka1.credit_service.dto.response.InfoResponseDto;
import com.banka1.credit_service.dto.response.UpdatedBalanceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Adapter that replaces the former HTTP REST client for account-service communication.
 * All methods now call AccountService Spring bean directly (inner call, no HTTP).
 */
@Service("creditAccountServiceAdapter")
@RequiredArgsConstructor
public class AccountService {

    private final com.banka1.account_service.service.AccountService internalAccountService;

    public InfoResponseDto getInfo(String fromBankNumber, String toBankNumber) {
        com.banka1.account_service.dto.response.InfoResponseDto r =
                internalAccountService.info(null, fromBankNumber, toBankNumber);
        return new InfoResponseDto(
                CurrencyCode.valueOf(r.getFromCurrencyCode().name()),
                CurrencyCode.valueOf(r.getToCurrencyCode().name()),
                r.getFromVlasnik(),
                r.getToVlasnik(),
                r.getFromEmail(),
                r.getFromUsername()
        );
    }

    public UpdatedBalanceResponseDto transfer(PaymentDto p) {
        com.banka1.account_service.dto.response.UpdatedBalanceResponseDto r =
                internalAccountService.transfer(new com.banka1.account_service.dto.request.PaymentDto(
                        p.getFromAccountNumber(), p.getToAccountNumber(),
                        p.getFromAmount(), p.getToAmount(), p.getCommission(), p.getClientId()
                ));
        return new UpdatedBalanceResponseDto(r.getSenderBalance(), r.getReceiverBalance());
    }

    public UpdatedBalanceResponseDto transaction(PaymentDto p) {
        com.banka1.account_service.dto.response.UpdatedBalanceResponseDto r =
                internalAccountService.transaction(new com.banka1.account_service.dto.request.PaymentDto(
                        p.getFromAccountNumber(), p.getToAccountNumber(),
                        p.getFromAmount(), p.getToAmount(), p.getCommission(), p.getClientId()
                ));
        return new UpdatedBalanceResponseDto(r.getSenderBalance(), r.getReceiverBalance());
    }

    public UpdatedBalanceResponseDto transactionFromBank(BankPaymentDto p) {
        // account-service's transactionFromBank returns void; callers don't use the return value
        internalAccountService.transactionFromBank(new com.banka1.account_service.dto.request.BankPaymentDto(
                p.getFromAccountNumber(), p.getToAccountNumber(), p.getAmount()
        ));
        return null;
    }

    public AccountDetailsResponseDto getDetails(String accountNumber) {
        InternalAccountDetailsDto r = internalAccountService.getAccountDetails(accountNumber);
        return new AccountDetailsResponseDto(
                r.ownerId(),
                CurrencyCode.valueOf(r.currency()),
                r.email(),
                r.username()
        );
    }
}
