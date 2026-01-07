package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.ingredients.CustomerDTO;
import app.interactions.RegisterCustomer;
import app.repository.CustomerRepo;
 
@Service
public class RegisterCustomerImpl implements RegisterCustomer { 

    private CustomerRepo _repo;

    public RegisterCustomerImpl(CustomerRepo repo) {
        _repo = repo;
    }

    @Override
    public RegisterResult apply(CustomerDTO customer) {
        
        try {
            var response = _repo.post(customer);
            var result = new JSONObject(response.body());
            if(response.statusCode() >= 200 && response.statusCode() < 300)
                return new RegisterCustomer.CustomerRegistered(result.getString("id"), customer);
             else
                return new RegisterCustomer.CustomerRejected(result.getString("error"));
        } catch (Throwable e) { 
            return new RegisterCustomer.CustomerRejected(e.getMessage());
        }
    }
}
