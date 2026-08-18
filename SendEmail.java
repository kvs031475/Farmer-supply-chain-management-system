

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    public static void main(String[] args) {
        // Sender's email credentials
        final String senderEmail = "sudarshan02testing@gmail.com";
        final String senderPassword = "yowe loaz trmp pjuw"; // Use App Password (not normal password)

        // Recipient email
        String recipientEmail = "sudarshanmohan63@gmail.com";

        // Email subject and content
        String subject = "Automated Email from Java";
        String body = "Hello,\n\nThis is an automated email sent using Java!\nBsdk kya kar raha hai madarchod\n\nGand mara mc,\nJava Mail Bot";

        sendEmail(senderEmail, senderPassword, recipientEmail, subject, body);
    }

    public static void sendEmail(String from, String password, String to, String subject, String text) {
        // SMTP server configuration for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            // Send the email
            Transport.send(message);
            System.out.println("✅ Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to send email.");
        }
    }
}
