package app.repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Repository;
import app.components.JSONing;
import app.http.OpenAccountRequest;

@Repository
public class AccountRepo {
    private static final String _endpoint = "http://localhost:8082/accounts";
    
    public HttpResponse<String> post(String customerId, String accountName) {
        var body = JSONing.toString(new OpenAccountRequest(accountName, "TRY", customerId));
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

    public HttpResponse<String> get(String accountId) {
        var request = HttpRequest.newBuilder(URI.create(_endpoint + "/" + accountId))
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
