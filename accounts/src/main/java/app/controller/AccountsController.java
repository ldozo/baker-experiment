
package app.controller;

import app.domain.Account;
import app.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountRepository repo;

    public AccountsController(AccountRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Account> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-customer/{customerId}")
    public List<Account> byCustomer(@PathVariable String customerId) {
        return repo.findByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Account input) {
        input.setId(null); // ULID via @PrePersist
        if (repo.existsByCustomerIdAndNameIgnoreCase(input.getCustomerId(), input.getName())) {
            throw new RuntimeException("account.conflict");
        }
        try {
            Account saved = repo.save(input);
            return ResponseEntity.created(URI.create("/accounts/" + saved.getId())).body(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody Account input) {
        return repo.findById(id).map(existing -> {
            if (repo.existsByCustomerIdAndNameIgnoreCaseAndIdNot(input.getCustomerId(), input.getName(), id)) {
                throw new RuntimeException("account.conflict");
            }
            existing.setName(input.getName());
            existing.setBalance(input.getBalance());
            existing.setCurrency(input.getCurrency());
            existing.setCustomerId(input.getCustomerId());
            return ResponseEntity.ok(repo.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body("{\"status\":\"ok\"}");
    }
}
