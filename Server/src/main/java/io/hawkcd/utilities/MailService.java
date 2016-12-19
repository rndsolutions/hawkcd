package io.hawkcd.utilities;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    public static void sendMail() {
        // Recipient's email ID needs to be mentioned.
        String to = "vventsislavov@rnd-solutions.net";

        // Sender's email ID needs to be mentioned
        String from = "vladislav.v.nikolov@gmail.com";

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is actual message");

            // Send message
//            Transport transport = session.getTransport("smtp");
//            transport.connect(null, login, pass);
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

//        Email from = new Email("hawkcdforum@gmail.com");
//        String subject = "Hello from Hawk";
//        Email to = new Email("spetrov@rnd-solutions.net");
//        Content content = new Content("text/plain", "HawkCD rocks and can now send emails!");
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
//        Request request = new Request();
//        try {
//            request.method = Method.POST;
//            request.endpoint = "mail/send";
//            request.body = mail.build();
//            Response response = sg.api(request);
//            System.out.println(response.statusCode);
//            System.out.println(response.body);
//            System.out.println(response.headers);
//        } catch (IOException ex) {
//            throw ex;
//        }
    }
}
