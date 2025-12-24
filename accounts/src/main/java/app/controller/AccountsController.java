
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
    public ResponseEntity<?> create(@Valid @RequestBody Account input) {
        input.setId(null); // ULID via @PrePersist
        if (repo.existsByCustomerIdAndNameIgnoreCase(input.getCustomerId(), input.getName())) {
            return ResponseEntity.status(409).body(err("account.conflict"));
        }
        try {
            Account saved = repo.save(input);
            return ResponseEntity.created(URI.create("/accounts/" + saved.getId())).body(saved);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body(err("account.conflict"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody Account input) {
        return repo.findById(id).map(existing -> {
            if (repo.existsByCustomerIdAndNameIgnoreCaseAndIdNot(input.getCustomerId(), input.getName(), id)) {
                return ResponseEntity.status(409).body(err("account.conflict"));
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

    private static String err(String key) {
        // Key is resolved to human text in GlobalExceptionHandler; if not used, returns the key.
        return "{\"error\":\"" + key + "\"}";
    }
}
