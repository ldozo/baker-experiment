
package app.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.domain.Customer;
import app.domain.Status;
import app.repository.CustomerRepository;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Customer> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable("id") String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Customer input) {
        // Ignore incoming id; ULID will be generated in @PrePersist
        input.setId(null);

        if (repo.existsByEmailIgnoreCase(input.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        try {
            Customer saved = repo.save(input);
            return ResponseEntity.created(URI.create("/customers/" + saved.getId())).body(saved);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody Customer input) {
        return repo.findById(id).map(existing -> {
            if (repo.existsByEmailIgnoreCaseAndIdNot(input.getEmail(), id)) {
                throw new RuntimeException("Email already exists");
            }
            existing.setFirstname(input.getFirstname());
            existing.setLastname(input.getLastname());
            existing.setEmail(input.getEmail());
            existing.setStatus(input.getStatus());
            Customer saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() { return ResponseEntity.ok().body("{\"status\":\"ok\"}"); }
}
