
package app.domain;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "email_log", indexes = {
    @Index(name = "idx_email_to", columnList = "to_address"),
    @Index(name = "idx_email_created", columnList = "created_at")
})
public class EmailLog {

    @Id
    @Column(length = 26, nullable = false)
    private String id; // ULID

    @NotBlank(message = "Recipient address is required")
    @Email(message = "Recipient address format is invalid")
    @Column(name = "to_address", nullable = false)
    private String to;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "Body is required")
    @Lob
    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EmailStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    public EmailLog() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) this.id = UlidCreator.getUlid().toString();
        if (this.createdAt == null) this.createdAt = Instant.now();
        if (this.to != null) this.to = this.to.trim();
        if (this.subject != null) this.subject = this.subject.trim();
    }

    // getters/setters
    public String getId() { return id; }
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public EmailStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
    public void setId(String id) { this.id = id; }
    public void setTo(String to) { this.to = to; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }
    public void setStatus(EmailStatus status) { this.status = status; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
