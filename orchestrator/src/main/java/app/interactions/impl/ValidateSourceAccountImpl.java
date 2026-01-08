package app.interactions.impl;

import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.MoneyTransferDTO;
import app.interactions.AccountValidationResult;
import app.interactions.ValidateSourceAccount;
import app.repository.AccountRepo;

@Service
public class ValidateSourceAccountImpl implements ValidateSourceAccount {

    private AccountRepo _repo;

    public ValidateSourceAccountImpl(AccountRepo repo) {
        _repo = repo;
    }

    @Override
    public AccountValidationResult apply(MoneyTransferDTO transfer) {
        HttpResponse<String> response;
        try {
            response = _repo.get(transfer.getSourceAccountId());
        } catch (Throwable e) { return new ValidateSourceAccount.SourceAccountFailed(transfer.getSourceAccountId(), e.getMessage());}

        var obj = new JSONObject(response.body());
        if(!(response.statusCode() >= 200 && response.statusCode() < 300)) {
            return new ValidateSourceAccount.SourceAccountFailed(transfer.getSourceAccountId(), obj.getString("error"));
        }

        if(obj.getBigDecimal("balance").compareTo(transfer.getAmount()) < 0) {    
            return new ValidateSourceAccount.SourceAccountFailed(transfer.getSourceAccountId(), "Source has no balance");
        }
        return new ValidateSourceAccount.SourceAccountValidated(obj.getString("customer_id"));
    }
}
