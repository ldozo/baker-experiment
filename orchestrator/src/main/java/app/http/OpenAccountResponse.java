package app.http;

public record OpenAccountResponse(
    String id,
    String name, 
    String currency,
    String customerId
) {

}
