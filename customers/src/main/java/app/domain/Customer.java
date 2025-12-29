
package app.domain;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customers_email", columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(length = 26, nullable = false)
    private String id; // ULID (26-char)

    @NotBlank
    @Column(nullable = false)
    private String firstname;

    @NotBlank
    @Column(nullable = false)
    private String lastname;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;
 
    @Column(nullable = false)
    private int age;

    @NotBlank
    @Column(nullable = false)
    private String nationality;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) 
    private Status status = Status.INITIAL;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UlidCreator.getUlid().toString();
        }
        if (firstname != null) firstname = firstname.trim();
        if (lastname != null) lastname = lastname.trim();
        if (email != null) email = email.trim();
    }
}
