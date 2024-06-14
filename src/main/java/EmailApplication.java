import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.mail.Message;
import javax.mail.MessagingException;

public class EmailApplication {
    private static EmailClient emailClient;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("EMAIL CLIENT APPLICATION");
        try {
            String emailAddress = "adrianchihai69@gmail.com";
            System.out.print("Logging in... " + emailAddress);
            String password = "nkxh dzvd syoj dihi";

         /* System.out.print("Enter your email: ");
            String emailAddress = reader.readLine();
            System.out.print("Enter your password: ");
            String password = reader.readLine(); */

            emailClient = new EmailClient(emailAddress, password);
            emailClient.login();
            System.out.println("\nLogin successful!");

            while (true) {
                System.out.println("\nOPTIONS: \n1. Send Email \n2. Send Email with Attachment \n3. List Inbox \n4. Quit");
                System.out.print("Choose an option: ");
                String option = reader.readLine();

                switch (option) {
                    case "1":
                        sendEmail(reader, false);
                        break;
                    case "2":
                        sendEmail(reader, true);
                        break;
                    case "3":
                        listInbox();
                        break;
                    case "4":
                        emailClient.logout();
                        return;
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            }
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
    }

    private static void sendEmail(BufferedReader reader, boolean withAttachment) throws IOException, MessagingException {
        System.out.print("Enter recipient's email: ");
        String toAddress = reader.readLine();
        System.out.print("Enter subject: ");
        String subject = reader.readLine();
        System.out.print("Enter body: ");
        String body = reader.readLine();

        if (withAttachment) {
            System.out.print("Enter attachment path: ");
            String attachmentPath = reader.readLine();
            emailClient.sendEmailWithAttachment(toAddress, subject, body, attachmentPath, null);
            System.out.println("Email with attachment sent successfully.");
        } else {
            emailClient.sendTextEmail(toAddress, subject, body, null);
            System.out.println("Email sent successfully.");
        }
    }

    private static void listInbox() throws MessagingException {
        Message[] messages = emailClient.getEmails("IMAP"); // for listing inbox
        System.out.println("Inbox Emails:");
        for (Message message : messages) {
            System.out.println("From: " + Arrays.toString(message.getFrom()));
            System.out.println("Subject: " + message.getSubject());
            System.out.println("---------");
        }
    }
}
