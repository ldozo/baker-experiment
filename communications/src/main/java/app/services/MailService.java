
package app.services;

import app.domain.EmailLog;
import app.domain.EmailStatus;
import app.domain.EmailTemplate;
import app.repository.EmailLogRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Map;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailLogRepository emailLogs;
    private final String from;

    public MailService(JavaMailSender mailSender,
                       TemplateEngine templateEngine,
                       EmailLogRepository emailLogs,
                       @Value("${mail.from}") String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailLogs = emailLogs;
        this.from = from;
    }

    /**
     * Renders a template, sends the mail, and persists EmailLog.
     */
    public EmailLog sendTemplatedMail(String to, EmailTemplate template, Map<String, Object> fields) {
        // Build subject/body from template
        String subject = subjectFor(template, fields);
        String bodyHtml = render(template, fields);

        EmailLog log = new EmailLog();
        log.setTo(to);
        log.setSubject(subject);
        log.setBody(bodyHtml);
        log.setStatus(EmailStatus.FAILED); // default until sent
        log.setCreatedAt(Instant.now());

        try {
            send(to, subject, bodyHtml);
            log.setStatus(EmailStatus.SENT);
            log.setSentAt(Instant.now());
        } catch (Exception ex) {
            log.setErrorMessage(ex.getMessage());
        }

        return emailLogs.save(log);
    }

    private void send(String to, String subject, String bodyHtml) throws Exception {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(bodyHtml, true); // HTML
        mailSender.send(msg);
    }

    private String render(EmailTemplate template, Map<String, Object> fields) {
        Context ctx = new Context();
        if (fields != null) fields.forEach(ctx::setVariable);
        String path = switch (template) {
            case WELCOME -> "email/welcome";
            case MONEY_TRANSFER -> "email/money_transfer";
        };
        return templateEngine.process(path, ctx);
    }

    private String subjectFor(EmailTemplate template, Map<String, Object> fields) {
        return switch (template) {
            case WELCOME -> "Welcome to Our Bank";
            case MONEY_TRANSFER -> {
                Object amount = fields != null ? fields.getOrDefault("amount", "") : "";
                Object currency = fields != null ? fields.getOrDefault("currency", "") : "";
                yield ("Money Transfer Confirmation" +
                       ((amount != null && !amount.toString().isBlank())
                           ? (" - " + amount + " " + currency) : ""));
            }
        };
    }

    /** Quick guard for required fields per template (optional, extend as needed). */
    public void validateRequired(EmailTemplate template, Map<String, Object> fields) {
        switch (template) {
            case WELCOME -> require(fields, "customerName");
            case MONEY_TRANSFER -> {
                require(fields, "customerName");
                require(fields, "amount");
                require(fields, "currency");
                require(fields, "toAccountName");
                require(fields, "reference");
            }
        }
    }

    private void require(Map<String, Object> fields, String key) {
        if (fields == null || fields.get(key) == null || fields.get(key).toString().isBlank()) {
            throw new ValidationException("Missing field: " + key);
        }
    }
}
