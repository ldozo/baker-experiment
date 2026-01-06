package app.interactions.impl;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.interactions.ValidateSourceAccount;
import app.repository.AccountRepo;

@Service
public class ValidateSourceAccountImpl implements ValidateSourceAccount {

    private AccountRepo _repo;

    public ValidateSourceAccountImpl(AccountRepo repo) {
        _repo = repo;
    }

    @Override
    public AccountValidationResult apply(String accountId, BigDecimal amount, String currency) {
        HttpResponse<String> response;
        try {
            response = _repo.get(accountId);
        } catch (Throwable e) { return new ValidateSourceAccount.SourceAccountFailed(e.getMessage());}

        var obj = new JSONObject(response.body());
        if(!(response.statusCode() >= 200 && response.statusCode() < 300)) {
            return new ValidateSourceAccount.SourceAccountFailed(obj.getString("error"));
        }

        if(obj.getBigDecimal("balance").compareTo(amount) < 0) {    
            return new ValidateSourceAccount.SourceAccountFailed("Source has no balance");
        }

        if(obj.getString("currency").equals(currency)) {
            return new ValidateSourceAccount.SourceAccountFailed("Source Currency Missmatch");
        }
        return new ValidateSourceAccount.SourceAccountValidated(obj.getString("customer_id"));
    }
}
