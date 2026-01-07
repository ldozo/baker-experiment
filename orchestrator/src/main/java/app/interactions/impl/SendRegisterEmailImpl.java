package app.interactions.impl;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import app.http.SendEmailRequest;
import app.ingredients.CustomerDTO;
import app.interactions.SendRegisterEmail;
import app.repository.EmailRepo;

@Service
public class SendRegisterEmailImpl implements SendRegisterEmail {
    
    private EmailRepo _repo;

    public SendRegisterEmailImpl(EmailRepo repo) {
        _repo = repo;
    }
    
    @Override
    public EmailResult apply(CustomerDTO customer) {
        var body = toRequest(customer);
        try {
            var response = _repo.post(body);
            var result = new JSONObject(response.body());
            if(response.statusCode() >= 200 && response.statusCode() < 300)
                return new SendRegisterEmail.EmailSent(result.getString("id"));
            else 
                return new SendRegisterEmail.EmailFailed(result.getString("error"));
        } catch (Throwable e) {
            System.err.println(e);
            return new SendRegisterEmail.EmailFailed(e.getMessage());
        }
    }

    private SendEmailRequest toRequest(CustomerDTO customer) {
        var fields = Map.of("firstname", customer.getFirstname(), 
                            "lastname", customer.getLastname(),
                            "email", customer.getEmail());
        var req = new SendEmailRequest(customer.getEmail(),  "WELCOME", fields);
        return req;
    }
}
