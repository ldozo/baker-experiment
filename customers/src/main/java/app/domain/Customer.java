
package app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customers_email", columnNames = "email")
})
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

    public Customer() {}

    public Customer(String id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UlidCreator.getUlid().toString();
        }
        if (firstname != null) firstname = firstname.trim();
        if (lastname != null) lastname = lastname.trim();
        if (email != null) email = email.trim();
    }

    public String getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public void setId(String id) { this.id = id; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setEmail(String email) { this.email = email; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    
}
