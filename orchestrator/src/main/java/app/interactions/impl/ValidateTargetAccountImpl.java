package app.interactions.impl;

import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.MoneyTransferDTO;
import app.interactions.AccountValidationResult;
import app.interactions.ValidateTargetAccount;
import app.repository.AccountRepo;

@Service
public class ValidateTargetAccountImpl implements ValidateTargetAccount {
    private AccountRepo _repo;

    public ValidateTargetAccountImpl(AccountRepo repo) {
        _repo = repo;
    }

    @Override
    public AccountValidationResult apply(MoneyTransferDTO transfer) {
        HttpResponse<String> response;
        try {
            response = _repo.get(transfer.getTargetAccountId());
        } catch (Throwable e) {
            return new ValidateTargetAccount.TargetAccountFailed(e.getMessage());
        }

        var obj = new JSONObject(response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return new ValidateTargetAccount.TargetAccountFailed(obj.getString("error"));
        }

        if (obj.getString("currency").equals(transfer.getCurrency())) {
            return new ValidateTargetAccount.TargetAccountFailed("Target Currency Missmatch");
        }
        return new ValidateTargetAccount.TargetAccountValidated(obj.getString("customer_id"));
    }

}
