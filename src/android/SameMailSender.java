package com.autentia.plugin.sendmail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SameMailSender extends javax.mail.Authenticator {

    private String port;
    private String password;

    private String from;
    private String mailhost;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public SameMailSender(String host, String user, String password, String port) {
            this.mailhost = host;
            this.from = user;
            this.password = password;
            this.port = port;
            Properties props = new Properties();

            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", mailhost);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", this.port);
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.quitwait", "false");
            
            // Prevent garbled attachment file name. 
            System.setProperty("mail.mime.encodefilename", "true");
            System.setProperty("mail.mime.charset", "UTF-8");
            
            session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(from, password);
    }

    public synchronized void sendMail(String to, String from, String subject, String body, List<Attachment> attachments)
            throws Exception {

        String sender = from;
        String recipients = to;

        // The message object
        MimeMessage message = new MimeMessage(session);
        // Add trivial information
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        if (recipients.indexOf(',') > 0) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        } else {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        }

        // Create multipart content.
        Multipart multipart = new MimeMultipart("mixed");

        // Add the body part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html")));
        multipart.addBodyPart(messageBodyPart);
        
        // Part two is attachment
        for (Attachment attachment : attachments) {
            if (attachment != null) {
                messageBodyPart = new MimeBodyPart();
                byte[] imgBytes = decodeBase64(attachment.getBase64Source());
                DataSource source = new ByteArrayDataSource(imgBytes, "image/*");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachment.getFileName());
                multipart.addBodyPart(messageBodyPart);
            }
        }
        // Put parts in message
        message.setContent(multipart);
        // Send the message.
        Transport.send(message);
    }

    private byte[] decodeBase64(String base64String){
        // The rules when using android.util.Base64
        // 1.Replace '+' to '-'
        // 2.Replace '/' to '_'
        base64String = base64String.replace('+', '-').replace('/', '_');
        return android.util.Base64.decode(base64String, android.util.Base64.URL_SAFE|android.util.Base64.NO_WRAP);
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
