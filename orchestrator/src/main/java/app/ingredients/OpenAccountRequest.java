package app.ingredients;

public record OpenAccountRequest(
    String name, 
    String currency,
    String customerId
) {

}
