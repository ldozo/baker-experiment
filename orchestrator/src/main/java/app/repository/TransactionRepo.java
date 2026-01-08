package app.repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepo {
    private static final String _endpoint = "http://localhost:8082/transactions";

    private HttpResponse<String> transaction(String direction, String accountId, BigDecimal amount) {
        var reqPayload = new JSONObject();
        reqPayload.put("amount", amount);
        reqPayload.put("accountId", accountId);
        var url = URI.create(_endpoint + "/" + direction); 
        var request = HttpRequest.newBuilder(url)
                                 .header("Content-Type", "application/json")
                                 .POST(HttpRequest.BodyPublishers.ofString(reqPayload.toString()))
                                 .build();
        try {
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> debit(String accountId, BigDecimal amount) {
        return transaction("debit", accountId, amount);
    }

    public HttpResponse<String> credit(String accountId, BigDecimal amount) {
        return transaction("credit", accountId, amount);
    }
}
