package edu.khai.csn.abondar.passwordmanager;

import java.security.Security;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.util.Log;


/**
 * Created by Alexey Bondar on 15-May-18.
 */

public class MailSender extends javax.mail.Authenticator {

    private String mLogin;
    private String mPassword;
    private String mailhost = "smtp.gmail.com";
    private Session session;
    private Multipart _multipart;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender(String login, String password) {
        mLogin = login;
        mPassword = password;

        _multipart = new MimeMultipart();

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mLogin, mPassword);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) {
        try {
            MimeMessage message = new MimeMessage(session);

            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(recipients));

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            _multipart.addBodyPart(messageBodyPart);

            message.setContent(_multipart);

            Transport.send(message);
        } catch (Exception e) {
            Log.e("sendMail", "Error sending email! ");
        }
    }
}
