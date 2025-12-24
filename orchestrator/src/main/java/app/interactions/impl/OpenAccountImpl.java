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

import app.ingredients.OpenAccountRequest;
import app.ingredients.OpenAccountResponse;
import app.interactions.OpenAccount;

public class OpenAccountImpl implements OpenAccount {
    @Value("${api.accounts}")
    private String endpoint;
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public Outcome apply(OpenAccountRequest customer) {
        var body = toBody(customer);
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var result = fromBody(response.body());
            if(StringUtils.hasText(result.id()))
            return new OpenAccount.AccountOpened(body);
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }


        return null;
    }

    private static String toBody(OpenAccountRequest cust) {
        try { return _mapper.writeValueAsString(cust); } 
        catch (JsonProcessingException e) { System.err.println(e); }
        return null;
    }

    private static OpenAccountResponse fromBody(String body) {
        try { return _mapper.readValue(body, OpenAccountResponse.class); }
        catch (Exception e) { System.err.println(e);}
        return null;
    }
}
