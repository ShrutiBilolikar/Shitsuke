package com.example.demo.config;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceInitializer implements CommandLineRunner {
    
    private final EmailService emailService;
    
    @Autowired
    public EmailServiceInitializer(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("EmailServiceInitializer: CommandLineRunner executed");
        System.out.println("EmailServiceInitializer: EmailService bean = " + (emailService != null ? "✓ EXISTS" : "✗ NULL"));
        if (emailService != null) {
            System.out.println("EmailServiceInitializer: EmailService class = " + emailService.getClass().getName());
        } else {
            System.err.println("EmailServiceInitializer: ✗✗✗ CRITICAL: EmailService bean is NULL!");
            System.err.println("EmailServiceInitializer: This means EmailService failed to be created.");
            System.err.println("EmailServiceInitializer: Check for missing dependencies or configuration errors.");
        }
        System.out.println("========================================");
    }
}

