
package app.domain;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions",
       indexes = {
         @Index(name = "idx_tx_account", columnList = "account_id"),
         @Index(name = "idx_tx_created", columnList = "created_at")
       })
public class Transaction {

    @Id
    @Column(length = 26, nullable = false)
    private String id; // ULID

    @NotBlank(message = "{tx.accountId.required}")
    @Column(name = "account_id", nullable = false, length = 26)
    private String accountId;

    @NotNull(message = "{tx.type.required}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @NotNull(message = "{tx.amount.required}")
    @DecimalMin(value = "0.01", inclusive = true, message = "{tx.amount.min}")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "{tx.currency.required}")
    @Pattern(regexp = "^[A-Z]{3}$", message = "{tx.currency.invalid}")
    @Column(nullable = false, length = 3)
    private String currency; // must match Account.currency

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Transaction() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UlidCreator.getUlid().toString();
        }
        if (this.createdAt == null) this.createdAt = Instant.now();
        trimFields();
    }

    @PreUpdate
    public void preUpdate() { trimFields(); }

    private void trimFields() {
        if (accountId != null) accountId = accountId.trim();
        if (currency != null) currency = currency.trim();
        if (description != null) description = description.trim();
    }

    // getters/setters
    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
