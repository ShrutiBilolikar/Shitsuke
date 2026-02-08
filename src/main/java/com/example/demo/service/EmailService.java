package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@org.springframework.context.annotation.Lazy(false)
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender, @Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        System.out.println("EmailService: Constructor called - Creating EmailService bean");
        System.out.println("EmailService: JavaMailSender = " + (mailSender != null ? "provided" : "NULL"));
        System.out.println("EmailService: TemplateEngine = " + (templateEngine != null ? "provided" : "NULL"));
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        System.out.println("EmailService: Constructor completed");
    }
    
    @PostConstruct
    public void init() {
        // Log configuration after all dependencies are injected
        System.out.println("========================================");
        System.out.println("EmailService: @PostConstruct called - Initialized");
        System.out.println("EmailService: From email: " + (fromEmail != null ? fromEmail : "✗ NULL"));
        System.out.println("EmailService: Frontend URL: " + (frontendUrl != null ? frontendUrl : "✗ NULL"));
        System.out.println("EmailService: MailSender: " + (mailSender != null ? "✓ Configured" : "✗ NULL"));
        System.out.println("EmailService: TemplateEngine: " + (templateEngine != null ? "✓ Configured" : "✗ NULL"));
        
        // Test if JavaMailSender is properly configured
        if (mailSender != null) {
            try {
                // This will fail if mail configuration is invalid, but won't crash the app
                System.out.println("EmailService: JavaMailSender class: " + mailSender.getClass().getName());
            } catch (Exception e) {
                System.err.println("EmailService: ✗ Error checking JavaMailSender: " + e.getMessage());
            }
        }
        System.out.println("========================================");
    }

    /**
     * Send a simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Send an HTML email using Thymeleaf template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            System.out.println("EmailService: Creating email message - From: " + fromEmail + ", To: " + to + ", Subject: " + subject);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            System.out.println("EmailService: Sending email via SMTP...");
            mailSender.send(mimeMessage);
            System.out.println("EmailService: Email sent successfully!");
        } catch (MessagingException e) {
            System.err.println("EmailService: MessagingException - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("EmailService: Unexpected error - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send friend request notification email
     */
    public void sendFriendRequestEmail(String recipientEmail, String senderEmail, String senderName) {
        System.out.println("EmailService: Preparing to send friend request email to: " + recipientEmail);
        Context context = new Context();
        context.setVariable("senderName", senderName != null ? senderName : senderEmail);
        context.setVariable("senderEmail", senderEmail);
        context.setVariable("frontendUrl", frontendUrl);
        context.setVariable("loginUrl", frontendUrl + "/login");
        
        String subject = senderName != null 
            ? senderName + " sent you a friend request on Shitsuke"
            : "You have a new friend request on Shitsuke";
        
        try {
            sendHtmlEmail(recipientEmail, subject, "friend-request", context);
            System.out.println("EmailService: ✓ Friend request email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("EmailService: ✗ Failed to send friend request email to " + recipientEmail + ": " + e.getMessage());
            throw e; // Re-throw so calling code knows it failed
        }
    }

    /**
     * Send friend request accepted notification email
     */
    public void sendFriendRequestAcceptedEmail(String recipientEmail, String accepterEmail, String accepterName) {
        System.out.println("EmailService: Preparing to send friend request accepted email to: " + recipientEmail);
        Context context = new Context();
        context.setVariable("accepterName", accepterName != null ? accepterName : accepterEmail);
        context.setVariable("accepterEmail", accepterEmail);
        context.setVariable("frontendUrl", frontendUrl);
        
        String subject = accepterName != null
            ? accepterName + " accepted your friend request"
            : "Your friend request was accepted";
        
        try {
            sendHtmlEmail(recipientEmail, subject, "friend-request-accepted", context);
            System.out.println("EmailService: ✓ Friend request accepted email sent successfully to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("EmailService: ✗ Failed to send friend request accepted email to " + recipientEmail + ": " + e.getMessage());
            throw e; // Re-throw so calling code knows it failed
        }
    }
}

