package app.interactions.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import app.ingredients.Customer;
import app.ingredients.SendEmailRequest; 
import app.interactions.SendRegisterEmail;

public class SendRegisterEmailImpl implements SendRegisterEmail {
    private String endpoint = "http://localhost:8090/email/send";
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public EmailResult apply(Customer customer) {
        var body = toBody(customer);
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var result = new JSONObject(response.body());
            if(response.statusCode() >= 200 && response.statusCode() < 300)
                return new SendRegisterEmail.EmailSent(result.getString("id"));
            else 
                return new SendRegisterEmail.EmailFailed(result.getString("error"));
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            return new SendRegisterEmail.EmailFailed(e.getMessage());
        }
    }

    private static String toBody(Customer customer) {
        var fields = Map.of("firstname", customer.getFirstname(), 
                            "lastname", customer.getLastname(),
                            "email", customer.getEmail());
        var req = new SendEmailRequest(customer.getEmail(),  "WELCOME", fields);

        try { return _mapper.writeValueAsString(req); } 
        catch (JsonProcessingException e) { System.err.println(e); }
        return null;
    }
}
