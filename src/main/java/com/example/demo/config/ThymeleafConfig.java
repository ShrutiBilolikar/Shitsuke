package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfig {
    
    @Bean
    public TemplateEngine emailTemplateEngine() {
        System.out.println("ThymeleafConfig: Creating emailTemplateEngine bean...");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());
        System.out.println("ThymeleafConfig: ✓ emailTemplateEngine bean created");
        return templateEngine;
    }
    
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        System.out.println("ThymeleafConfig: Creating emailTemplateResolver bean...");
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // Disable cache for development
        System.out.println("ThymeleafConfig: ✓ emailTemplateResolver bean created");
        return templateResolver;
    }
}

