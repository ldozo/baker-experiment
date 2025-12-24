package app.interactions.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import app.ingredients.RegisterCustomerRequested;
import app.ingredients.SendEmailRequest;
import app.interactions.SendEmail;

public class SendEmailImpl implements SendEmail {
    @Value("${api.email}")
    private String endpoint;
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public EmailResult apply(SendEmailRequest customer) {
        var body = toBody(customer);
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            fromBody(response.body());
            return new SendEmail.EmailResult("ok");
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }


        return null;
    }

    private static String toBody(SendEmailRequest cust) {
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
