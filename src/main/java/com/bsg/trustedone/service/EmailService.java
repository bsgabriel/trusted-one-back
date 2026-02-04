package com.bsg.trustedone.service;

import com.bsg.trustedone.exception.EmailException;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final Emails resendEmailService;
    private final MessageService messageService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.sender}")
    private String mailSender;

    public void sendPasswordResetTemplate(String to, String token) throws EmailException {
        var parameters = new HashMap<String, Object>();
        parameters.put("{greeting}", messageService.getMessage("user.password-reset.email.greeting"));
        parameters.put("{message}", messageService.getMessage("user.password-reset.email.message"));
        parameters.put("{resetLink}", String.format("%s/#/nova-senha?token=%s", frontendUrl, token));
        parameters.put("{buttonText}", messageService.getMessage("user.password-reset.email.button-text"));
        parameters.put("{expirationNote}", messageService.getMessage("user.password-reset.email.expiration-note"));
        parameters.put("{ignoreNote}", messageService.getMessage("user.password-reset.email.ignore-note"));

        var subject = messageService.getMessage("user.password-reset.email.subject");
        var template = loadTemplate("password-reset.html", parameters);
        sendEmail(to, subject, template);
    }

    private String loadTemplate(String templateName, Map<String, Object> parameters) throws EmailException {
        var resource = new ClassPathResource("templates/email/" + templateName);

        String template;
        try {
            template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EmailException("Failed to read email template: " + templateName, e);
        }

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            template = template.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return template;
    }

    private void sendEmail(String to, String subject, String content) throws EmailException {
        try {
            resendEmailService.send(CreateEmailOptions.builder()
                    .from(mailSender)
                    .to(to)
                    .subject(subject)
                    .html(content)
                    .build());
        } catch (ResendException e) {
            throw new EmailException("Failed to send email to: " + to, e);
        }
    }
}