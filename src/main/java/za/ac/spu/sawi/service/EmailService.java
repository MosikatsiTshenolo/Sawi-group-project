package za.ac.spu.sawi.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import za.ac.spu.sawi.entity.User;

@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:}")
    private String fromAddress;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean isConfigured() {
        return mailSenderProvider.getIfAvailable() != null
                && fromAddress != null && !fromAddress.isBlank();
    }

    public boolean send(String recipient, String subject, String body) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || fromAddress == null || fromAddress.isBlank()
                || recipient == null || recipient.isBlank()) {
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            return false;
        }
    }

    public boolean sendStudentApproval(User user) {
        return send(user.getEmail(), "Your SAWI account is approved",
                "Hello " + user.getFirstName() + ",\n\n"
                        + "Your student account has been approved. You can now log in to SAWI at "
                        + baseUrl + "/login using your registered username and password.");
    }

    public boolean sendStaffAccountCreated(User user) {
        return send(user.getEmail(), "Your SAWI staff account was created",
                "Hello " + user.getFirstName() + ",\n\n"
                        + "Your " + user.getRole().getRoleName().toLowerCase()
                        + " account was created by the SAWI administrator. Your username is "
                        + user.getUsername() + ". Sign in at " + baseUrl
                        + "/login with the temporary password provided to you securely.");
    }

    public boolean sendStaffNotification(User recipient, User sender,
            String title, String message) {
        return send(recipient.getEmail(), title,
                "Hello " + recipient.getFirstName() + ",\n\n"
                        + sender.getRole().getRoleName() + " "
                        + sender.getFirstName() + " " + sender.getLastName()
                        + " sent you an update:\n\n" + message
                        + "\n\nView it in SAWI: " + baseUrl + "/notifications");
    }

    public boolean sendPasswordResetApproval(User user, String resetUrl) {
        return send(user.getEmail(), "Your SAWI password reset is approved",
                "Hello " + user.getFirstName() + ",\n\n"
                        + "An administrator approved your password reset request. Use this one-time link within 30 minutes:\n"
                        + resetUrl + "\n\nIf you did not request this, contact the SAWI administrator.");
    }
}