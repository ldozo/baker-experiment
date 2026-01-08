package app.interactions.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import app.ingredients.MoneyTransferDTO;
import app.interactions.ValidateTransaction;

@Service
public class ValidateTransactionImpl implements ValidateTransaction {

    @Override
    public TransactionValidationResult apply(MoneyTransferDTO transfer) {
        if(transfer.getAmount().compareTo(BigDecimal.ZERO) < 1) {
            return new ValidateTransaction.TransactionFailed("Invalid amount");
        }

        return new ValidateTransaction.TransactionValidated();
    }
   
}
