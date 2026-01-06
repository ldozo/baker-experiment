package app.interactions.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import app.interactions.ValidateTransaction;

@Service
public class ValidateTransactionImpl implements ValidateTransaction {

    @Override
    public TransactionValidationResult apply(BigDecimal amount, String currency) {
        if(!currency.equals("try")) {
            return new ValidateTransaction.TransactionFailed("Currency not allowed to transfer");
        }
        if(amount.compareTo(BigDecimal.ZERO) < 1) {
            return new ValidateTransaction.TransactionFailed("Invalid amount");
        }

        return new ValidateTransaction.TransactionValidated();
    }
   
}
