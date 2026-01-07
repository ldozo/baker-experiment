package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.MoneyTransferDTO;
import app.interactions.DebitAccount;
import app.repository.TransactionRepo;

@Service
public class DebitAccountImpl implements DebitAccount {

    private TransactionRepo _repo;

    public DebitAccountImpl(TransactionRepo repo) {
        _repo = repo;
    }

    @Override
    public DebitAccountResult apply(MoneyTransferDTO transfer) {
        var response = _repo.debit(transfer.getSourceAccountId(), transfer.getAmount(), transfer.getCurrency());
        var obj = new JSONObject(response.body());
        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            return new DebitAccount.DebitFailed(obj.getString("error"));
        }
        return new DebitAccount.DebitSucceed(obj.getString("id"));
    }
 
}
