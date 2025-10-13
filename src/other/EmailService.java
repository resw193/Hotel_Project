package other;


import gui.login.forms.Login;

import javax.mail.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

// Gửi OTP qua SMTP (Gmail), tạo App Password và điền vào APP_PASSWORD
public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String FROM_EMAIL = "" + Login.email;
    private static final String FROM_NAME  = "Mimosa Hotel";
    private static final String APP_PASSWORD = "gigp rpnu vbuk dynt";   // đặt app password

    public static void sendOtp(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException {
        Session session = createSession();
        Message msg = new javax.mail.internet.MimeMessage(session);
        msg.setFrom(new javax.mail.internet.InternetAddress(FROM_EMAIL, FROM_NAME));
        msg.setRecipients(Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(toEmail));
        msg.setSubject("[Mimosa Hotel] Mã OTP đặt lại mật khẩu");
        msg.setText("Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút.");
        Transport.send(msg);
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        return Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }
}
