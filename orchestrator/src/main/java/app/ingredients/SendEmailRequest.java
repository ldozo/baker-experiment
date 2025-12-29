package app.ingredients;

import java.util.Map;

public record SendEmailRequest(
    String toAddress,
    String template,
    Map<String, String> fields
) {

}
