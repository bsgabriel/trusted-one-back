package com.bsg.trustedone.configuration;

import com.resend.Resend;
import com.resend.services.emails.Emails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${app.mail.resend.api-key}")
    private String resendApiKey;

    @Bean
    public Emails resendEmailService() {
        return new Resend(resendApiKey).emails();
    }

}
