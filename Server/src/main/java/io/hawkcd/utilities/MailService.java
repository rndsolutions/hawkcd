package io.hawkcd.utilities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    public static void sendMail(String recipient, String subject, String content) {
        // Sender's email ID needs to be mentioned, TODO: Extract this into config file
        String from = "hawkcdforum@gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server, TODO: Extract this into config file
        properties.setProperty("mail.smtp.host", "smtp.sendgrid.net");
        properties.setProperty("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.auth", "true");

        // Set up authentication, TODO: Extract this into config file, if no value is specified, try to get system environment variable for security
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("apikey", System.getenv("SENDGRID_API_KEY"));
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(content);

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
