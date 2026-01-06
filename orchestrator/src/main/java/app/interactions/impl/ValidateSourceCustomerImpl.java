package app.interactions.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.interactions.ValidateSourceCustomer;
import app.repository.CustomerRepo;

@Service
public class ValidateSourceCustomerImpl implements ValidateSourceCustomer {

    private CustomerRepo _customers;

    public ValidateSourceCustomerImpl(CustomerRepo customers) {
        _customers = customers;
    }

    @Override
    public CustomerValidationResult apply(String customerId) {
        var response = _customers.get(customerId);
        var customer = new JSONObject(response.body());
        if(response.statusCode() < 200 || response.statusCode() >= 300) {
            return new ValidateSourceCustomer.SourceCustomerFailed(customer.getString("error"));
        }

        if(!customer.getString("status").equals("active")) {
            return new ValidateSourceCustomer.SourceCustomerFailed("Source customer has invalid status");
        }

        return new ValidateSourceCustomer.SourceCustomerValidated();
    }
   
}
