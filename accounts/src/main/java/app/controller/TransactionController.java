
package app.controller;

import app.domain.Account;
import app.domain.Transaction;
import app.domain.TransactionType;
import app.repository.AccountRepository;
import app.repository.TransactionRepository;
import jakarta.validation.Valid;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public ResponseEntity<?> debit(@RequestBody Transaction input) {
        return apply(input, TransactionType.DEBIT);
    }

    @PostMapping("/credit")
    @Transactional
    public ResponseEntity<?> credit(@RequestBody Transaction input) {
        return apply(input, TransactionType.CREDIT);
    }

    @PostMapping("/rollback/{id}")
    @Transactional
    public ResponseEntity<?> postMethodName(@PathVariable("id") String transactionId) {
        var otx = txRepo.findById(transactionId);
        if(otx.isEmpty()) {
            throw new RuntimeException("Invalid transaction id");
        }
        var tx = otx.get();
        if(tx.getType() == TransactionType.DEBIT)
            apply(tx, TransactionType.CREDIT);
        else 
            apply(tx, TransactionType.DEBIT);
        return ResponseEntity.ok().build();
    }
    

    // ---- Internal ----

    private ResponseEntity<?> apply(Transaction input, TransactionType type) {
        // Ensure account exists
        var oAccount = accountRepo.findById(input.getAccountId());
        if (oAccount.isEmpty()) {
             throw new RuntimeException("Invalid Account");
        }
        // Amount > 0 already validated by @DecimalMin
        var account = oAccount.get();
        BigDecimal amount = input.getAmount();
        if (type == TransactionType.CREDIT) {
            account.setBalance(account.getBalance().add(amount));
        } else { // CREDIT
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(amount));
        }

        // Persist account update and transaction
        accountRepo.save(account);
        input.setType(type);
        input.setCurrency(account.getCurrency());
        Transaction saved = txRepo.save(input);

        return ResponseEntity.created(URI.create("/transactions/" + saved.getId())).body(saved);
    }
}
