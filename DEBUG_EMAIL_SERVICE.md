# Debug EmailService - Step by Step

## Problem
EmailService initialization logs are not appearing on startup.

## What I Added

I've added comprehensive logging at every step of bean creation:

1. **ThymeleafConfig** - Logs when email template beans are created
2. **EmailService Constructor** - Logs when EmailService bean is being created
3. **EmailService @PostConstruct** - Logs after all dependencies are injected
4. **EmailServiceInitializer** - Logs when CommandLineRunner executes

## How to See the Logs

### Step 1: Rebuild the Backend
```bash
docker-compose down
docker-compose build --no-cache app
docker-compose up -d
```

### Step 2: Check All Logs (Not Filtered)
```bash
# PowerShell
docker-compose logs app --tail=100

# Or follow logs in real-time
docker-compose logs -f app
```

### Step 3: Look for These Log Messages

You should see logs in this order:

1. **ThymeleafConfig logs:**
   ```
   ThymeleafConfig: Creating emailTemplateResolver bean...
   ThymeleafConfig: ✓ emailTemplateResolver bean created
   ThymeleafConfig: Creating emailTemplateEngine bean...
   ThymeleafConfig: ✓ emailTemplateEngine bean created
   ```

2. **EmailService Constructor:**
   ```
   EmailService: Constructor called - Creating EmailService bean
   EmailService: JavaMailSender = provided
   EmailService: TemplateEngine = provided
   EmailService: Constructor completed
   ```

3. **EmailService @PostConstruct:**
   ```
   ========================================
   EmailService: @PostConstruct called - Initialized
   EmailService: From email: shrutibilolikar2003@gmail.com
   EmailService: Frontend URL: http://localhost:5173
   EmailService: MailSender: ✓ Configured
   EmailService: TemplateEngine: ✓ Configured
   EmailService: JavaMailSender class: org.springframework.mail.javamail.JavaMailSenderImpl
   ========================================
   ```

4. **EmailServiceInitializer (after app starts):**
   ```
   ========================================
   EmailServiceInitializer: CommandLineRunner executed
   EmailServiceInitializer: EmailService bean = ✓ EXISTS
   EmailServiceInitializer: EmailService class = com.example.demo.service.EmailService
   ========================================
   ```

## If You Still Don't See Logs

### Check 1: Is the backend running?
```bash
docker-compose ps
```

### Check 2: Are there compilation errors?
```bash
docker-compose logs app | Select-String -Pattern "error|Error|ERROR|exception|Exception|EXCEPTION"
```

### Check 3: Is JavaMailSender being auto-configured?
Look for Spring Boot startup logs that mention "MailSender" or "mail".

### Check 4: Check if EmailService is being created at all
```bash
docker-compose logs app | Select-String -Pattern "EmailService|ThymeleafConfig|EmailServiceInitializer"
```

## Possible Issues

1. **JavaMailSender not auto-configured**
   - Check if `spring-boot-starter-mail` is in `pom.xml` ✓ (it is)
   - Check if mail properties are in `application.properties` ✓ (they are)

2. **TemplateEngine bean not found**
   - Check if `ThymeleafConfig` is being scanned (it's in `com.example.demo.config` package)
   - Check if `@Qualifier("emailTemplateEngine")` matches the bean name

3. **Bean creation failure**
   - Look for exceptions in logs
   - Check if all dependencies are available

## Next Steps

1. **Rebuild and restart** (see Step 1 above)
2. **Check unfiltered logs** (see Step 2 above)
3. **Share the logs** - especially any errors or if you see the ThymeleafConfig logs but not EmailService logs

