package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.interactions.ValidateTargetCustomer;
import app.repository.CustomerRepo;

@Service
public class ValidateTargetCustomerImpl implements ValidateTargetCustomer {

    private CustomerRepo _customers;

    public ValidateTargetCustomerImpl(CustomerRepo customers) {
        _customers = customers;
    }

    @Override
    public CustomerValidationResult apply(String customerId) {
        var response = _customers.get(customerId);
        var customer = new JSONObject(response.body());
        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            return new ValidateTargetCustomer.SourceCustomerFailed(customer.getString("error"));
        }

        if(!customer.getString("status").equals("active")) {
            return new ValidateTargetCustomer.SourceCustomerFailed("Source customer has invalid status");
        }

        return new ValidateTargetCustomer.SourceCustomerValidated();
    }
   
}
