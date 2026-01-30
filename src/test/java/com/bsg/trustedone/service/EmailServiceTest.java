package com.bsg.trustedone.service;

import com.bsg.trustedone.exception.EmailException;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private Emails resendEmailService;

    @Mock
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://frontend.com");
        ReflectionTestUtils.setField(emailService, "mailSender", "no-reply@test.com");
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void sendPasswordResetTemplate_withValidData_shouldSendEmail() throws Exception {
        var to = "user@test.com";
        var token = "token";

        when(messageService.getMessage(anyString())).thenReturn("message");

        emailService.sendPasswordResetTemplate(to, token);

        verify(messageService, atLeastOnce()).getMessage(anyString());
        verify(resendEmailService, times(1)).send(any(CreateEmailOptions.class));
    }

    @Test
    @DisplayName("Should throw EmailException when template cannot be loaded")
    void sendPasswordResetTemplate_whenTemplateNotFound_shouldThrowEmailException() throws EmailException {
        var to = "user@test.com";
        var token = "token";

        var serviceSpy = Mockito.spy(emailService);
        doThrow(new EmailException("error"))
                .when(serviceSpy)
                .sendPasswordResetTemplate(anyString(), anyString());

        assertThatThrownBy(() -> serviceSpy.sendPasswordResetTemplate(to, token)).isInstanceOf(EmailException.class);
    }

    @Test
    @DisplayName("Should throw EmailException when email provider fails")
    void sendPasswordResetTemplate_whenResendFails_shouldThrowEmailException() throws Exception {
        var to = "user@test.com";
        var token = "token";

        when(messageService.getMessage(anyString())).thenReturn("message");

        doThrow(new ResendException("error"))
                .when(resendEmailService)
                .send(any(CreateEmailOptions.class));

        assertThatThrownBy(() -> emailService.sendPasswordResetTemplate(to, token)).isInstanceOf(EmailException.class);
        verify(resendEmailService, times(1)).send(any(CreateEmailOptions.class));
    }
}
