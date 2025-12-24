
package app.controller;

import app.domain.EmailLog;
import app.domain.EmailTemplate;
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
@RequestMapping("/emails")
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
    public ResponseEntity<EmailLog> send(
            @RequestParam @NotBlank @Email String to,
            @RequestParam @NotBlank String template,
            @RequestBody(required = false) Map<String, Object> fields) {

        EmailTemplate tpl = EmailTemplate.valueOf(template.toUpperCase());
        mail.validateRequired(tpl, fields);
        EmailLog log = mail.sendTemplatedMail(to, tpl, fields);
        return ResponseEntity.ok(log);
    }
}
