/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.fileOperations.EmailBodyToPDF;
import com.model.EmailModel;
import com.sql.EMail;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

/**
 *
 * @author Andrew
 */
public class recieveEmail {
    
    static String authUser = "";
    static String authPass = "";
    static String emailUsername = "";
    static String emailPassword = "";
    static String emailHost = "";
    static String submitterName = "";
    static int emailPort = 0;
    static int lengthMsgs;
    boolean textIsHtml = false;
    
    private void emailConnectionProperties() {
        authUser = ""; //  User  "serb.testdocket"
        authPass = ""; //  Pass  "xln.1211"
        emailPassword = "";
        emailUsername = "";
        emailHost = "";
        emailPort = 0;
        submitterName = "";
    }
        
    public void fetchEmail() {
        emailConnectionProperties();
        
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(
                  authUser, authPass);
            }
         };
        
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.imap.submitter", submitterName);
            properties.setProperty("mail.imap.auth", "true");
            properties.setProperty("mail.imap.host", emailHost);
            properties.put("mail.imap.port", String.valueOf(emailPort));
            properties.put("mail.imap.fetchsize", "965536");
            
            properties.setProperty("mail.store.protocol", "imaps");
            
            Session session = Session.getInstance(properties, auth);
            Store store = session.getStore();
            store.connect(emailHost, emailUsername, emailPassword);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message[] msgs = inbox.search(unseenFlagTerm);
            //Message[] msgs = inbox.getMessages();
            lengthMsgs = msgs.length;
            inbox.setFlags(msgs, new Flags(Flags.Flag.SEEN), true);
            if (msgs.length != 0) {
                for (Message msg : msgs) {
                    EmailModel eml = new EmailModel();
                    eml.setSection("");
                    eml = saveEnvelope(msg, msg, eml);
                    eml = EmailBodyToPDF.createEmailBody(eml);
                    int emailID = EMail.InsertEmail(eml);
                    saveAttachments(msg, msg, emailID);
                    //Add attachments to DB
                }
            }
            inbox.close(false);
            store.close();

        } catch (Exception ex) {
            if (ex != null) {
                System.out.println("<html><center>Unable to connect to email Server.<br>"
                        + "Please ensure you are connected to the network and press OK and try again.</center></html>");
            }
        }
    }
    
    private EmailModel saveEnvelope(Message m, Part p, EmailModel eml) {
        try {
            Address[] address;
            //From
            if ((address = m.getFrom()) != null) {
                for (Address addres : address) {
                    eml.setEmailFrom(addres.toString());
                }
            }
            //to
            if ((address = m.getRecipients(Message.RecipientType.TO)) != null) {
                String to = "";
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        to = address[j].toString();
                    } else {
                        to += "; " + address[j].toString();
                    }
                }
                eml.setEmailTo(removeEmojiAndSymbolFromString(to));
            }
            //CC
            if ((address = m.getRecipients(Message.RecipientType.CC)) != null) {
                String cc = "";
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        cc = address[j].toString();
                    } else {
                        cc += "; " + address[j].toString();
                    }
                }
                eml.setEmailCC(removeEmojiAndSymbolFromString(cc));
            }
            //BCC
            if ((address = m.getRecipients(Message.RecipientType.BCC)) != null) {
                String bcc = "";
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        bcc = address[j].toString();
                    } else {
                        bcc += "; " + address[j].toString();
                    }
                }
                eml.setEmailBCC(removeEmojiAndSymbolFromString(bcc));
            }
            //subject
            if (m.getSubject() == null) {
                eml.setEmailSubject("");
            } else {
                eml.setEmailSubject(removeEmojiAndSymbolFromString(m.getSubject().replace("'", "\"")));
            }

            //date
            eml.setSentDate(new java.sql.Timestamp(m.getSentDate().getTime()));
            eml.setReceivedDate(new java.sql.Timestamp(m.getReceivedDate().getTime()));
            
            eml.setEmailBody(removeEmojiAndSymbolFromString(getEmailBodyText(p)));

        } catch (MessagingException ex) {
            System.err.println("CRASH");
        }
        return eml;
    }
    
    private String getEmailBodyText(Part p) {
        try {
            if (p.isMimeType("text/*")) {
                String s = (String) p.getContent();
                textIsHtml = p.isMimeType("text/html");
                return s;
            }
            
            if (p.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                Multipart mp = (Multipart) p.getContent();
                String text = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        if (text == null) {
                            text = getEmailBodyText(bp);
                        }
                    } else if (bp.isMimeType("text/html")) {
                        String s = getEmailBodyText(bp);
                        if (s != null) {
                            return s;
                        }
                    } else {
                        return getEmailBodyText(bp);
                    }
                }
                return text;
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getEmailBodyText(mp.getBodyPart(i));
                    if (s != null) {
                        return s;
                    }
                }
            }
            return null;
        } catch (MessagingException | IOException ex) {
            System.err.println("CRASH");
        }
        return "";
    }
  
    

    private void saveAttachments(Part p, Message m, int emailID) {
        try {
            String ct = p.getContentType();
            String filename = p.getFileName();
            if (filename != null && !filename.endsWith("vcf")) {
                /*
                 DOES NOT CAST one particular docx file properly
                 java.lang.ClassCastException: com.sun.mail.imap.IMAPMessage cannot be cast to javax.mail.internet.MimeBodyPart
                 */
                try {
                    ((MimeBodyPart) p).saveFile("C:\\" + filename.replace("/", "-"));
                    //writeEmailAttachment(filename.replace("/", "-").replace(":", ""));
                } catch (ClassCastException ex) {
                    System.err.println("Attachment \"" + filename + "\" could not be saved");
                }
            } else if (p.isMimeType("IMAGE/*")) {
                if (p.isMimeType("IMAGE/JPEG")) {
                    try {
                        String ext = p.getContentType().substring(p.getContentType().lastIndexOf('/') + 1);
                        ext = ext.toLowerCase();
                        filename = "image" + new Date().getTime() + "." + ext;
                        //((MimeBodyPart)p).saveFile(global.emailPath + filename.replace("/", "-"));
                        //save the image as an attachment
                        saveFile(p, filename);
                    } catch (ClassCastException ex) {
                        System.err.println("Attachment \"" + filename + "\" could not be saved");
                    }
                }
            }
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                int pageCount = mp.getCount();
                for (int i = 0; i < pageCount; i++) {
                    saveAttachments(mp.getBodyPart(i), m, emailID);
                }
            } else if (p.isMimeType("message/rfc822")) {  //mail header
                saveAttachments((Part) p.getContent(), m, emailID);
            }
        } catch (MessagingException | IOException ex) {
            System.err.println("CRASH");
        }
    }

    
    private String removeEmojiAndSymbolFromString(String content) {
        String utf8tweet = "";
        try {
            byte[] utf8Bytes = content.getBytes("UTF-8");
            utf8tweet = new String(utf8Bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("CRASH");
        }
        Pattern unicodeOutliers = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE
                | Pattern.CANON_EQ
                | Pattern.CASE_INSENSITIVE
        );
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);
        utf8tweet = unicodeOutlierMatcher.replaceAll(" ");

        return utf8tweet;
    }
    
    private void saveFile(Part p, String filename) {
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("C:\\" + filename))));
            try (BASE64DecoderStream test = (BASE64DecoderStream) p.getContent()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = test.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.close();
            //writeEmailAttachment(filename.replace("/", "-").replace(":", "").replace("\"", ""));
        } catch (FileNotFoundException ex) {
            System.err.println("CRASH");
        } catch (IOException | MessagingException ex) {
            System.err.println("CRASH");
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                System.err.println("CRASH");
            }
        }
    }
}
