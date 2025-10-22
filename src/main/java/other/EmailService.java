package other;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    // Tài khoản gửi cố định (gửi cho người quản lý)
    private static final String SENDER_EMAIL = System.getenv().getOrDefault("MAIL_USER", "baodinh.nguyen321@gmail.com");
    private static final String SENDER_NAME  = "Mimosa Hotel";

    private static final String APP_PASSWORD = System.getenv().getOrDefault("MAIL_APP_PASSWORD", "gigprpnuvbukdynt");

    public static void sendOtp(String toEmail, String otp)
            throws MessagingException, UnsupportedEncodingException {

        Session session = createSession();

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("[Mimosa Hotel] Mã OTP đặt lại mật khẩu", "UTF-8");
        msg.setText("Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút.", "UTF-8");

        Transport.send(msg);
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        return Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });
    }
}
