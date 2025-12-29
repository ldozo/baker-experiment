
package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);
}
