package com.smartwallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /* ==========================================
                    OTP EMAIL
    ========================================== */

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("NeuroWallet OTP Verification");

        message.setText(
                "Your OTP is: " + otp +
                "\n\nOTP is valid for 5 minutes."
        );

        mailSender.send(message);
    }

    /* ==========================================
             PASSWORD RESET EMAIL
    ========================================== */

    public void sendPasswordResetEmail(String toEmail, String token) {

        try {

            String resetLink =
                    "http://localhost:3000/reset-password?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);

            helper.setSubject("Reset Your NeuroWallet Password");

            String html =
                    "<html>" +
                    "<body style='font-family:Arial;background:#f4f7fb;padding:40px;'>" +

                    "<div style='max-width:650px;margin:auto;background:#ffffff;padding:40px;border-radius:12px;'>" +

                    "<h1 style='color:#6d5efc;'>NeuroWallet</h1>" +

                    "<h2>Password Reset Request</h2>" +

                    "<p>We received a request to reset your password.</p>" +

                    "<p>Click the button below to continue.</p>" +

                    "<a href='" + resetLink + "' " +

                    "style='display:inline-block;" +
                    "padding:15px 30px;" +
                    "background:#6d5efc;" +
                    "color:white;" +
                    "text-decoration:none;" +
                    "border-radius:8px;" +
                    "font-weight:bold;'>"

                    +

                    "Reset Password"

                    +

                    "</a>"

                    +

                    "<p style='margin-top:30px;'>"

                    +

                    "If you did not request this password reset, simply ignore this email."

                    +

                    "</p>"

                    +

                    "<hr>"

                    +

                    "<small>© NeuroWallet AI Fintech Platform</small>"

                    +

                    "</div>"

                    +

                    "</body>"

                    +

                    "</html>";

            helper.setText(html, true);

            mailSender.send(message);

        }

        catch (MessagingException e) {

            throw new RuntimeException(

                    "Unable to send password reset email",

                    e

            );

        }

    }

}