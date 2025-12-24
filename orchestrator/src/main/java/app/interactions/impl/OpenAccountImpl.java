package app.interactions.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import app.ingredients.OpenAccountRequest;
import app.interactions.OpenAccount;

public class OpenAccountImpl implements OpenAccount {
    @Value("${api.accounts}")
    private String endpoint;
    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    @Override
    public OpenAccountResult apply(String customerId, String name, String currency) {
        var body = toBody(new OpenAccountRequest(name, currency, customerId));
        var request = HttpRequest.newBuilder(URI.create(endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            var result = new JSONObject(response.body());
            if(response.statusCode() == 200)
                return new OpenAccount.AccountOpened(result.getString("id"));
            else 
                return new OpenAccount.AccountFailed("open-account-failed");    
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            return new OpenAccount.AccountFailed(e.getMessage());
        }
    }

    private static String toBody(OpenAccountRequest openAccountRequest) {
        try { return _mapper.writeValueAsString(openAccountRequest); } 
        catch (JsonProcessingException e) { System.err.println(e); }
        return null;
    }
}
