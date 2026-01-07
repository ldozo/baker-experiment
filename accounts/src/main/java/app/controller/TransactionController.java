
package app.controller;

import app.domain.Account;
import app.domain.Transaction;
import app.domain.TransactionType;
import app.repository.AccountRepository;
import app.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    public TransactionController(AccountRepository accountRepo, TransactionRepository txRepo) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
    }

    // ---- Queries ----

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable String id) {
        return txRepo.findById(id)
                     .map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-account/{accountId}")
    public List<Transaction> byAccount(@PathVariable String accountId) {
        return txRepo.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    // ---- Commands ----

    @PostMapping("/debit")
    @Transactional
    public ResponseEntity<?> debit(@Valid @RequestBody Transaction input) {
        return apply(input, TransactionType.DEBIT);
    }

    @PostMapping("/credit")
    @Transactional
    public ResponseEntity<?> credit(@Valid @RequestBody Transaction input) {
        return apply(input, TransactionType.CREDIT);
    }

    // ---- Internal ----

    private ResponseEntity<?> apply(Transaction input, TransactionType type) {
        // Ensure account exists
        Account account = accountRepo.findById(input.getAccountId()).orElse(null);
        if (account == null) {
             throw new RuntimeException("Invalid Account");
        }
        // Currency must match account
        if (!account.getCurrency().equalsIgnoreCase(input.getCurrency())) {
            throw new RuntimeException("Currency Mismatch");
        }
        // Amount > 0 already validated by @DecimalMin

        BigDecimal amount = input.getAmount();
        if (type == TransactionType.DEBIT) {
            account.setBalance(account.getBalance().add(amount));
        } else { // CREDIT
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(amount));
        }

        // Persist account update and transaction
        accountRepo.save(account);
        input.setType(type); // ignore incoming type, enforce endpoint semantics
        Transaction saved = txRepo.save(input);

        return ResponseEntity.created(URI.create("/transactions/" + saved.getId())).body(saved);
    }
}
