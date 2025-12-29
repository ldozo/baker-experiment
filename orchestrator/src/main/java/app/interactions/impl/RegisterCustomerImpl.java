package app.interactions.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import app.ingredients.Customer;
import app.interactions.RegisterCustomer;
 
public class RegisterCustomerImpl implements RegisterCustomer {

    private static final String endpoint = "http://localhost:8081/customers";
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public RegisterResult apply(Customer customer) {
        var body = toBody(customer);
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var result = new JSONObject(response.body());
            if(response.statusCode() >= 200 && response.statusCode() < 300)
                return new RegisterCustomer.CustomerRegistered(result.getString("id"), customer);
             else
                return new RegisterCustomer.CustomerRejected(result.getString("error"));
        } catch (Throwable e) { 
            return new RegisterCustomer.CustomerRejected(e.getMessage());
        }
    }

    private static String toBody(Customer cust) {
        try { return _mapper.writeValueAsString(cust); }
        catch (JsonProcessingException e) { System.err.println(e); }
        return null;
    } 
}
