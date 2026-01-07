package app.http;

public record OpenAccountRequest(
    String name, 
    String currency,
    String customerId
) {

}
