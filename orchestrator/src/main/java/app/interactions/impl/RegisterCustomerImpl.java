package app.interactions.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
 
import app.ingredients.RegisterCustomerRequested;
import app.interactions.RegisterCustomer;

public class RegisterCustomerImpl implements RegisterCustomer {
    @Value("${api.customers}")
    private String endpoint;
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public Outcome apply(RegisterCustomerRequested customer) {
        var body = toBody(customer);
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var result = fromBody(response.body());
            if(StringUtils.hasText(result.getId())) 
                return new RegisterCustomer.CustomerRegistered(result.getId());
            else 
                return new RegisterCustomer.CustomerRejected("");
        } catch (IOException | InterruptedException e) {
            return new RegisterCustomer.CustomerRejected(e.getMessage());
        }
    }

    private static String toBody(RegisterCustomerRequested cust) {
        try { return _mapper.writeValueAsString(cust); } 
        catch (JsonProcessingException e) { System.err.println(e); }
        return null;
    }

    private static RegisterCustomerRequested fromBody(String body) {
        try { return _mapper.readValue(body, RegisterCustomerRequested.class); }
        catch (Exception e) { System.err.println(e);}
        return null;
    }
}
