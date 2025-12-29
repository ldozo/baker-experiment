
package app.domain;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_accounts_customer_name", columnNames = { "customer_id", "name" })
}, indexes = {
        @Index(name = "idx_accounts_customer", columnList = "customer_id")
})
public class Account {

    @Id
    @Column(length = 26, nullable = false)
    private String id; // ULID

    @NotBlank(message = "{account.name.required}")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "{account.balance.required}")
    @DecimalMin(value = "0.00", inclusive = true, message = "{account.balance.min}")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotBlank(message = "{account.currency.required}")
    @Pattern(regexp = "^[A-Z]{3}$", message = "{account.currency.invalid}")
    @Column(nullable = false, length = 3)
    private String currency; // ISO-4217 code, e.g., TRY, USD, EUR

    @NotBlank(message = "{account.customerId.required}")
    @Column(name = "customer_id", nullable = false, length = 26)
    private String customerId; // foreign ULID (no FK constraint in this service)

    @Version
    private Long version;

    public Account() {
    }

    public Account(String id, String name, BigDecimal balance, String currency, String customerId) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.customerId = customerId;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UlidCreator.getUlid().toString();
        }
        trimFields();
    }

    @PreUpdate
    public void preUpdate() {
        trimFields();
    }

    private void trimFields() {
        if (name != null)
            name = name.trim();
        if (currency != null)
            currency = currency.trim();
        if (customerId != null)
            customerId = customerId.trim();
    }

    // getters/setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
