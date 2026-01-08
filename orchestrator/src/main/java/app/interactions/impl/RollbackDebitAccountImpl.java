package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.interactions.RollbackDebitAccount;
import app.repository.TransactionRepo;

@Service
public class RollbackDebitAccountImpl implements RollbackDebitAccount {

    private TransactionRepo _repo;

    public RollbackDebitAccountImpl(TransactionRepo repo) {
        _repo = repo;
    }

    @Override
    public DebitRollbackResult apply(String transactionId) {
        var response = _repo.rollbackDebit(transactionId);
        var obj = new JSONObject(response.body());
        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            return new RollbackDebitAccount.DebitRollbackFailed(obj.getString("error"));
        }
        return new RollbackDebitAccount.DebitRollbackSucceed();
    }
 
}
