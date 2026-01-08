package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.MoneyTransferDTO;
import app.interactions.CreditAccount;
import app.repository.TransactionRepo;

@Service
public class CreditAccountImpl implements CreditAccount {

    private TransactionRepo _repo;

    public CreditAccountImpl(TransactionRepo repo) {
        _repo = repo;
    }

    @Override
    public CreditAccountResult apply(MoneyTransferDTO transfer) {
        var response = _repo.credit(transfer.getTargetAccountId(), transfer.getAmount());
        var obj = new JSONObject(response.body());
        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            return new CreditAccount.CreditFailed(obj.getString("error"));
        }
        return new CreditAccount.CreditSucceed(obj.getString("id"));
    }
 
}
