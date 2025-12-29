package app.domain;

import java.util.Map;

public record SendEmailRequest(
    String toAddress,
    String template,
    Map<String, Object> fields
) {

}