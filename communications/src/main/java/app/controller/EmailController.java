
package app.controller;

import app.domain.EmailLog;
import app.domain.EmailTemplate;
import app.domain.SendEmailRequest;
import app.repository.EmailLogRepository;
import app.services.MailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@Validated
public class EmailController {

    private final MailService mail;
    private final EmailLogRepository repo;

    public EmailController(MailService mail, EmailLogRepository repo) {
        this.mail = mail;
        this.repo = repo;
    }

    // Query logs
    @GetMapping
    public List<EmailLog> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<EmailLog> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Send templated email and persist log
    @PostMapping("/send")
    public ResponseEntity<EmailLog> send(@RequestBody SendEmailRequest req) {

        EmailTemplate tpl = EmailTemplate.valueOf(req.template().toUpperCase());
        mail.validateRequired(tpl, req.fields());
        EmailLog log = mail.sendTemplatedMail(req.toAddress(), tpl, req.fields());
        return ResponseEntity.ok(log);
    }
}
