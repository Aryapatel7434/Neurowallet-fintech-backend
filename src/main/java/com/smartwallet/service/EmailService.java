package com.smartwallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
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
helper.setFrom("arypatel913@gmail.com");
helper.setReplyTo("arypatel913@gmail.com");
           helper.setSubject("NeuroWallet Security • Password Reset Request");

  String html =
    "<html>" +
    "<body style='margin:0;padding:0;background:#f5f7fb;font-family:Arial,sans-serif;'>" +

    "<table width='100%' cellpadding='0' cellspacing='0'>" +
    "<tr><td align='center'>" +

    "<table width='620' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;margin:40px 0;overflow:hidden;border:1px solid #e5e7eb;'>"

    +

    "<tr style='background:#6d5efc;'>"
    +

    "<td style='padding:24px;color:white;font-size:28px;font-weight:bold;'>"
    +

    "NeuroWallet"
    +

    "</td></tr>"

    +

    "<tr><td style='padding:35px;'>"

    +

    "<h2 style='margin-top:0;color:#111827;'>Password Reset Request</h2>"

    +

    "<p style='color:#4b5563;font-size:16px;'>"

    +

    "We received a request to reset the password for your NeuroWallet account."

    +

    "</p>"

    +

    "<p style='color:#4b5563;'>"

    +

    "Click the secure button below to continue."

    +

    "</p>"

    +

    "<p style='text-align:center;margin:35px 0;'>"

    +

    "<a href='" + resetLink + "' style='background:#6d5efc;color:#ffffff;text-decoration:none;padding:15px 30px;border-radius:8px;font-size:16px;font-weight:bold;'>"

    +

    "Reset Password"

    +

    "</a>"

    +

    "</p>"

    +

    "<p style='font-size:14px;color:#6b7280;'>"

    +

    "This reset link expires in <strong>15 minutes</strong>."

    +

    "</p>"

    +

    "<p style='font-size:14px;color:#6b7280;'>"

    +

    "If you did not request this password reset, you can safely ignore this email."

    +

    "</p>"

    +

    "<hr style='margin:30px 0;border:none;border-top:1px solid #e5e7eb;'>"

    +

    "<p style='font-size:13px;color:#9ca3af;'>"

    +

    "NeuroWallet Security Team<br>"

    +

    "This is an automated email. Please do not reply."

    +

    "</p>"

    +

    "</td></tr>"

    +

    "</table>"

    +

    "</td></tr>"

    +

    "</table>"

    +

    "</body></html>";
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