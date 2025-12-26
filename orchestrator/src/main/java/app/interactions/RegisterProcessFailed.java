package app.interactions;

public record RegisterProcessFailed(
        String reason
) implements RegisterProcessResult {
}
