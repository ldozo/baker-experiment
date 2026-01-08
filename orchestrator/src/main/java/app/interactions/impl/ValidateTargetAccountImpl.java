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
            return new ValidateTargetAccount.TargetAccountFailed(transfer.getTargetAccountId(), e.getMessage());
        }

        var obj = new JSONObject(response.body());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return new ValidateTargetAccount.TargetAccountFailed(transfer.getTargetAccountId(), obj.getString("error"));
        }
        return new ValidateTargetAccount.TargetAccountValidated(obj.getString("customerId"));
    }

}
