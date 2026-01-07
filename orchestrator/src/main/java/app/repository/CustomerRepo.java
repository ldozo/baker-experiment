package app.repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Repository;

import app.components.JSONing;
import app.ingredients.CustomerDTO;

@Repository
public class CustomerRepo {
    private static final String _endpoint = "http://localhost:8081/customers";
   

    public HttpResponse<String> post(CustomerDTO customer) {
        var body = JSONing.toString(customer);
        var request = HttpRequest.newBuilder(URI.create(_endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(body))
                                 .build();
        try {
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public HttpResponse<String> get(String customerId) {
        var request = HttpRequest.newBuilder(URI.create(_endpoint + "/" + customerId))
                                 .header("Content-Type", "application/json")
                                 .GET()
                                 .build();
        try {
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }   
}
