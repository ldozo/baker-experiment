
package app.repository;

import app.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
    boolean existsByCustomerIdAndNameIgnoreCase(String customerId, String name);
    boolean existsByCustomerIdAndNameIgnoreCaseAndIdNot(String customerId, String name, String id);
}
