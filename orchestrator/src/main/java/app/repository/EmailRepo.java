package app.repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Repository;

import app.components.JSONing;
import app.http.SendEmailRequest;

@Repository
public class EmailRepo {

    private static final String _endpoint = "http://localhost:8090/email/send";

    public HttpResponse<String> post(SendEmailRequest payload) {
        var tmp = JSONing.toString(payload);
        var request = HttpRequest.newBuilder(URI.create(_endpoint))
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(tmp))
                                 .build();
        try {
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
