package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.Customer;
import app.interactions.OpenAccount;
import app.repository.AccountRepo;

@Service
public class OpenAccountImpl implements OpenAccount {


    private AccountRepo _repo;

    public OpenAccountImpl(AccountRepo repo) {
        _repo = repo;
    }

    @Override
    public OpenAccountResult apply(String customerId, Customer customer) {
        var accountName = customerId + "-" + "TRY";
        
        try {
            var response = _repo.post(customerId, accountName); 
            var result = new JSONObject(response.body());
            if(response.statusCode() >= 200 && response.statusCode() < 300)
                return new OpenAccount.AccountOpened(result.getString("id"));
            else 
                return new OpenAccount.AccountFailed(result.getString("error"));    
        } catch (Throwable e) {
            System.err.println(e);
            return new OpenAccount.AccountFailed(e.getMessage());
        }
    }
}
