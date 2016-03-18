/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.fileOperations.EmailBodyToPDF;
import com.model.EmailMessageModel;
import com.model.SystemEmailModel;
import com.sql.EMail;
import com.sun.mail.util.BASE64DecoderStream;
import com.util.Global;
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
import net.htmlparser.jericho.Source;

/**
 *
 * @author Andrew
 */
public class recieveEmail {
                
    public static void fetchEmail(SystemEmailModel account) {
        Authenticator auth = setEmailAuthenticator(account);
        Properties properties = setEmailProperties(account);
                
        if (Global.isTestEmail()){
            auth = setRemoteEmailAuthenticator(account);
            properties = setRemoteEmailProperties(account);
        }
        
        try {  
            Session session = Session.getInstance(properties, auth);
            Store store = session.getStore();
            store.connect(account.getIncomingURL(), account.getIncomingPort(), account.getUsername(), account.getPassword());
            Folder fetchFolder = store.getFolder(account.getIncomingFolder());
            if (!"".equals(account.getIncomingFolder().trim())){
                fetchFolder = store.getFolder("INBOX");
            }
            
            fetchFolder.open(Folder.READ_WRITE);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message[] msgs = fetchFolder.search(unseenFlagTerm);
            //Message[] msgs = inbox.getMessages();
            fetchFolder.setFlags(msgs, new Flags(Flags.Flag.SEEN), true);
            if (msgs.length != 0) {
                for (Message msg : msgs) {
                    EmailMessageModel eml = new EmailMessageModel();
                    String emailTime = String.valueOf(new Date().getTime());
                    eml.setSection(account.getSection());
                    eml = saveEnvelope(msg, msg, eml);
                    eml = EmailBodyToPDF.createEmailBody(eml, emailTime);

                    int emailID = EMail.InsertEmail(eml);
                    System.err.println("emailID: " + emailID);
//                    saveAttachments(msg, msg, emailID);
                    //Add attachments to DB
                }
            }
            fetchFolder.close(false);
            store.close();

        } catch (Exception ex) {
            if (ex != null) {
                System.out.println("Unable to connect to email Server for: " 
                        + account.getEmailAddress()
                        + "\nPlease ensure you are connected to the network and"
                        + " try again.");
            }
        }
    }

    private static Authenticator setEmailAuthenticator(SystemEmailModel account) {
        Authenticator auth = new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        account.getUsername(), account.getPassword());
            }
        };
        return auth;
    }

    private static Authenticator setRemoteEmailAuthenticator(SystemEmailModel account) {
        Authenticator auth = new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "em.ohio.gov\\" + account.getUsername(), account.getPassword());
            }
        };
        return auth;
    }
    
    private static Properties setEmailProperties(SystemEmailModel account) {
        Properties properties = new Properties();
        
        properties.setProperty("mail.store.protocol", account.getIncomingProtocol());
        if (null != account.getIncomingProtocol())switch (account.getIncomingProtocol()) {
            case "imap":
            case "imaps":
                properties.setProperty("mail.imap.submitter", account.getUsername());
                properties.setProperty("mail.imap.auth", "true");
                properties.setProperty("mail.imap.host", account.getIncomingURL());
                properties.put("mail.imap.port", String.valueOf(account.getIncomingPort()));
                properties.put("mail.imap.fetchsize", "965536");
                break;
            case "pop":
                properties.setProperty("mail.pop3s.host", account.getIncomingURL());
                properties.put("mail.pop3s.port", String.valueOf(account.getIncomingPort()));
                properties.put("mail.pop3s.starttls.enable", "true");                
                break;
            default:
                break;
        }
        if (Global.isDebug() == true){
            properties.setProperty("mail.debug", "true");
        }
        return properties;
    }

    private static Properties setRemoteEmailProperties(SystemEmailModel account) {
        Properties properties = new Properties();
        
        properties.setProperty("mail.store.protocol", account.getIncomingProtocol());
        if (null != account.getIncomingProtocol())switch (account.getIncomingProtocol()) {
            case "imap":
            case "imaps":
                properties.setProperty("mail.imap.submitter", "serb.testdocket");
                properties.setProperty("mail.imap.auth", "true");
                properties.setProperty("mail.imap.host", "outlook.em.ohio.gov");
                properties.put("mail.imap.port", "143");
                properties.put("mail.imap.fetchsize", "965536");
                properties.put("mail.imap.auth.mechanisms","NTLM");
                properties.put("mail.imap.auth.ntlm.domain","outlook.em.ohio.gov");
                break;
            default:
                break;
        }
        if (Global.isDebug() == true){
            properties.setProperty("mail.debug", "true");
        }
        return properties;
    }
    
    private static EmailMessageModel saveEnvelope(Message m, Part p, EmailMessageModel eml) {
        String to = "";
        String cc = "";
        String bcc = "";
        
        try {
            Address[] address;
            //From
            if ((address = m.getFrom()) != null) {
                for (Address addy : address) {
                    eml.setEmailFrom(addy.toString());
                }
            }
            //to
            if ((address = m.getRecipients(Message.RecipientType.TO)) != null) {        
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        to = address[j].toString();
                    } else {
                        to += "; " + address[j].toString();
                    }
                }
            }
            eml.setEmailTo(removeEmojiAndSymbolFromString(to));
            //CC
            if ((address = m.getRecipients(Message.RecipientType.CC)) != null) {
                
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        cc = address[j].toString();
                    } else {
                        cc += "; " + address[j].toString();
                    }
                }
            }
            eml.setEmailCC(removeEmojiAndSymbolFromString(cc));
            //BCC
            if ((address = m.getRecipients(Message.RecipientType.BCC)) != null) {
                for (int j = 0; j < address.length; j++) {
                    if (j == 0) {
                        bcc = address[j].toString();
                    } else {
                        bcc += "; " + address[j].toString();
                    }
                }
            }
            eml.setEmailBCC(removeEmojiAndSymbolFromString(bcc));
            //subject
            if (m.getSubject() == null) {
                eml.setEmailSubject("");
            } else {
                eml.setEmailSubject(removeEmojiAndSymbolFromString(m.getSubject().replace("'", "\"")));
            }

            //date
            eml.setSentDate(new java.sql.Timestamp(m.getSentDate().getTime()));
            eml.setReceivedDate(new java.sql.Timestamp(m.getReceivedDate().getTime()));
            
            Source htmlSource = new Source(getEmailBodyText(p));
            String emailBody = htmlSource.getRenderer().toString();            
            eml.setEmailBody(removeEmojiAndSymbolFromString(emailBody));

        } catch (MessagingException ex) {
            System.err.println("CRASH");
        }
        return eml;
    }
    
    private static String getEmailBodyText(Part p) {
        try {
            if (p.isMimeType("text/*")) {
                String s = (String) p.getContent();
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
  
    private static void saveAttachments(Part p, Message m, int emailID) {
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
    
    private static String removeEmojiAndSymbolFromString(String content) {
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
    
    private static void saveFile(Part p, String filename) {
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
