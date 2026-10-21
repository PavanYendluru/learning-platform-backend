package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${app.mail.dev-simulation-enabled:true}")
    private boolean devSimulationEnabled;

    @Override
    public void sendRegistrationOtp(String toEmail, String name, String otp, int validityMinutes) {
        String subject = "Verify your email for LearnHub";
        String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background: #ffffff;">
                <h2 style="color: #4f46e5; margin-top: 0;">Welcome to LearnHub</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Thank you for registering. Use the following 4-digit verification code to complete your registration:</p>
                <div style="text-align: center; margin: 28px 0;">
                    <span style="display: inline-block; font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #1e1b4b; background: #e0e7ff; padding: 12px 28px; border-radius: 8px; border: 1px dashed #6366f1;">%s</span>
                </div>
                <p style="color: #64748b; font-size: 14px;">This code will expire in <strong>%d minutes</strong>.</p>
                <p style="color: #94a3b8; font-size: 12px; margin-top: 24px; border-top: 1px solid #f1f5f9; padding-top: 12px;">If you did not request this verification code, please ignore this email.</p>
            </div>
            """.formatted(name != null && !name.isBlank() ? name : "Learner", otp, validityMinutes);

        sendMailInternal(toEmail, subject, htmlBody, otp);
    }

    @Override
    public void sendPasswordResetOtp(String toEmail, String otp, int validityMinutes) {
        String subject = "Reset your password for LearnHub";
        String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 12px; background: #ffffff;">
                <h2 style="color: #dc2626; margin-top: 0;">Password Reset Request</h2>
                <p>We received a request to reset your password on LearnHub.</p>
                <p>Use the following 4-digit code to verify your identity and set a new password:</p>
                <div style="text-align: center; margin: 28px 0;">
                    <span style="display: inline-block; font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #1e1b4b; background: #fee2e2; padding: 12px 28px; border-radius: 8px; border: 1px dashed #ef4444;">%s</span>
                </div>
                <p style="color: #64748b; font-size: 14px;">This code will expire in <strong>%d minutes</strong>.</p>
                <p style="color: #94a3b8; font-size: 12px; margin-top: 24px; border-top: 1px solid #f1f5f9; padding-top: 12px;">If you did not request a password reset, your account is safe and you can ignore this email.</p>
            </div>
            """.formatted(otp, validityMinutes);

        sendMailInternal(toEmail, subject, htmlBody, otp);
    }

    private void sendMailInternal(String toEmail, String subject, String htmlBody, String rawOtp) {
        // Developer fallback simulation
        if (devSimulationEnabled || mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            log.info("===============================================================");
            log.info(" [EMAIL-SIMULATION] To: {}", toEmail);
            log.info(" [EMAIL-SIMULATION] Subject: {}", subject);
            log.info(" [EMAIL-SIMULATION] 4-DIGIT OTP CODE: >>> {} <<<", rawOtp);
            log.info("===============================================================");
        }

        // If credentials are configured, dispatch real email
        if (mailUsername != null && !mailUsername.isBlank() && mailPassword != null && !mailPassword.isBlank()) {
            try {
                String cleanPassword = mailPassword.trim().replaceAll("\\s+", "");
                Properties props = new Properties();
                props.put("mail.smtp.host", mailHost);
                props.put("mail.smtp.port", String.valueOf(mailPort));
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.trust", "*");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUsername.trim(), cleanPassword);
                    }
                });

                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(mailUsername.trim(), "LearnHub Platform"));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                msg.setSubject(subject, "UTF-8");
                msg.setContent(htmlBody, "text/html; charset=UTF-8");

                Transport.send(msg);
                log.info("Real email successfully sent to: {}", toEmail);
            } catch (Exception ex) {
                log.error("Failed to send real email to {}: {}. Note: Dev simulation logged OTP above.", toEmail, ex.getMessage(), ex);
            }
        }
    }
}
