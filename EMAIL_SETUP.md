# Email Configuration Setup

This application sends email notifications for friend requests. Follow these steps to configure email sending:

## Option 1: Gmail SMTP (Recommended for Development)

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate an App Password**:
   - Go to your Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Copy the 16-character password

3. **Update `application.properties`**:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-character-app-password
   ```

## Option 2: Other SMTP Providers

### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

### SendGrid
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=your-sendgrid-api-key
```

### Mailtrap (For Testing)
```properties
spring.mail.host=smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your-mailtrap-username
spring.mail.password=your-mailtrap-password
```

## Option 3: Disable Email (Development Only)

If you don't want to configure email right now, the application will still work. Email sending failures are caught and logged but don't break the friend request functionality.

To completely disable email:
1. Comment out the email sending code in `FriendshipService.java`
2. Or set `spring.mail.host` to an invalid value to fail fast

## Testing

After configuration, test by:
1. Sending a friend request to a valid email address
2. Check the recipient's inbox (and spam folder)
3. Check application logs for any email errors

## Troubleshooting

- **Authentication failed**: Check your username/password
- **Connection timeout**: Check firewall/network settings
- **Emails going to spam**: Configure SPF/DKIM records for production
- **Gmail "Less secure app" error**: Use App Passwords, not regular password

