package app.interactions.impl;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.interactions.ValidateTargetAccount;
import app.repository.AccountRepo;

@Service
public class ValidateTargetAccountImpl implements ValidateTargetAccount {
    private AccountRepo _repo;

    public ValidateTargetAccountImpl(AccountRepo repo) {
        _repo = repo;
    }

    @Override
    public AccountValidationResult apply(String accountId, BigDecimal amount, String currency) {
        HttpResponse<String> response;
        try {
            response = _repo.get(accountId);
        } catch (Throwable e) {
            return new ValidateTargetAccount.TargetAccountFailed(e.getMessage());
        }

        var obj = new JSONObject(response.body());
        if (!(response.statusCode() >= 200 && response.statusCode() < 300)) {
            return new ValidateTargetAccount.TargetAccountFailed(obj.getString("error"));
        }

        if (obj.getString("currency").equals(currency)) {
            return new ValidateTargetAccount.TargetAccountFailed("Currency Missmatch");
        }
        return new ValidateTargetAccount.TargetAccountValidated(accountId);
    }

}
