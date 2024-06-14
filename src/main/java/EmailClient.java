import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;

public class EmailClient {
    private String emailAddress;
    private String password;
    private String imapServer = "imap.gmail.com";
    private String popServer = "pop.gmail.com";
    private String smtpServer = "smtp.gmail.com";
    private int imapPort = 993;
    private int popPort = 995;
    private int smtpPort = 587;
    private Store imapStore;
    private Store popStore;
    private Session session;

    public EmailClient(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public void login() throws MessagingException {
        Properties props = new Properties();

        // IMAP
        props.setProperty("mail.imap.host", imapServer);
        props.setProperty("mail.imap.port", String.valueOf(imapPort));
        props.setProperty("mail.imap.ssl.enable", "true");
        Session imapSession = Session.getInstance(props);
        imapStore = imapSession.getStore("imap");
        imapStore.connect(emailAddress, password);

        // POP3
        props.setProperty("mail.pop3.host", popServer);
        props.setProperty("mail.pop3.port", String.valueOf(popPort));
        props.setProperty("mail.pop3.ssl.enable", "true");
        Session popSession = Session.getInstance(props);
        popStore = popSession.getStore("pop3");
        popStore.connect(emailAddress, password);

        // SMTP
        props.setProperty("mail.smtp.host", smtpServer);
        props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, password);
            }
        });
    }

    public Message[] getEmails(String protocol) throws MessagingException {
        Folder folder = null;
        if (protocol.equalsIgnoreCase("IMAP")) {
            folder = imapStore.getFolder("INBOX");
        } else if (protocol.equalsIgnoreCase("POP3")) {
            folder = popStore.getFolder("INBOX");
        }
        if (folder != null) {
            folder.open(Folder.READ_ONLY);
            return folder.getMessages();
        }
        return new Message[0];
    }

    public void sendTextEmail(String toAddress, String subject, String body, String replyTo) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        message.setSubject(subject);
        if (replyTo != null) {
            message.setReplyTo(InternetAddress.parse(replyTo));
        }
        message.setText(body);

        Transport.send(message);
    }

    public void sendEmailWithAttachment(String toAddress, String subject, String body, String attachmentPath, String replyTo) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        message.setSubject(subject);
        if (replyTo != null) {
            message.setReplyTo(InternetAddress.parse(replyTo));
        }

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File(attachmentPath));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);

    }

    public void logout() throws MessagingException {
        if (imapStore != null && imapStore.isConnected()) {
            imapStore.close();
        }
        if (popStore != null && popStore.isConnected()) {
            popStore.close();
        }
    }
}
