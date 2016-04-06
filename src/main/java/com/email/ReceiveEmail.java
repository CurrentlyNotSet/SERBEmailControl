/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.fileOperations.EmailBodyToPDF;
import com.fileOperations.ImageToPDF;
import com.fileOperations.TXTtoPDF;
import com.fileOperations.WordToPDF;
import com.model.EmailMessageModel;
import com.model.SystemEmailModel;
import com.sql.EMail;
import com.sql.EmailAttachment;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import com.util.StringUtilities;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Andrew
 */
public class ReceiveEmail {
    
    private static int attachmentCount;
    private static List<String> attachmentList;
                
    public static void fetchEmail(SystemEmailModel account) {
        Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
        Properties properties = EmailProperties.setEmailInProperties(account);
        
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
                    attachmentCount = 1;
                    attachmentList = new ArrayList<>();
                    EmailMessageModel eml = new EmailMessageModel();
                    String emailTime = String.valueOf(new Date().getTime());
                    eml.setSection(account.getSection());
                    eml = saveEnvelope(msg, msg, eml);
                    eml.setId(EMail.InsertEmail(eml));
                    saveAttachments(msg, msg, eml);
                    eml = EmailBodyToPDF.createEmailBodyIn(eml, emailTime, attachmentList);
                    eml.setReadyToFile(1);
                    EMail.setEmailReadyToFile(eml);
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
            ExceptionHandler.Handle(ex);
        }
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

            String emailBody = getEmailBodyText(p);
            if (StringUtilities.isHtml(emailBody)) {
                Source htmlSource = new Source(getEmailBodyText(p));
                Segment htmlSeg = new Segment(htmlSource, 0, htmlSource.length());
                Renderer htmlRend = new Renderer(htmlSeg);
                emailBody = htmlRend.toString();
            }

            eml.setEmailBody(removeEmojiAndSymbolFromString(emailBody));

        } catch (MessagingException ex) {
            ExceptionHandler.Handle(ex);
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
            ExceptionHandler.Handle(ex);
        }
        return "";
    }
  
    private static void saveAttachments(Part p, Message m, EmailMessageModel eml) {
        try {
            String filename = p.getFileName();
            if (filename != null && !filename.endsWith("vcf")) {
                try {
                    saveFile(p, filename, eml);
                } catch (ClassCastException ex) {
                    System.err.println("CRASH");
                }
            } else if (p.isMimeType("IMAGE/*")) {
                saveFile(p, filename, eml);
            }
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                int pageCount = mp.getCount();
                for (int i = 0; i < pageCount; i++) {
                    saveAttachments(mp.getBodyPart(i), m, eml);
                }
            } else if (p.isMimeType("message/rfc822")) {
                saveAttachments((Part) p.getContent(), m, eml);
            }
        } catch (IOException | MessagingException ex) {
            ExceptionHandler.Handle(ex);
        }
    }

    private static void saveFile(Part part, String filename, EmailMessageModel eml) {
        int i = attachmentCount++;
        String filePath = Global.getEmailPath() + eml.getSection() + File.separatorChar;
        if (FileService.isValidAttachment(filename)) {
            String extension = FilenameUtils.getExtension(filename);
            String fileNameDB = StringUtilities.properAttachmentName(filename, eml.getId(), i);

            if (saveAttachment(part, filePath, fileNameDB)) {
                if (FileService.isImageFormat(filename)) {
                    fileNameDB = ImageToPDF.createPDFFromImage(filePath, fileNameDB);
                } else if ("docx".equals(extension) || "doc".equals(extension)) {
                    fileNameDB = WordToPDF.createPDF(filePath, fileNameDB);
                } else if ("txt".equals(extension)) {
                    fileNameDB = TXTtoPDF.createPDF(filePath, fileNameDB);
                }
                if (!"".equals(fileNameDB)) {
                    attachmentList.add(fileNameDB);
                    EmailAttachment.insertEmailAttachment(eml.getId(), fileNameDB);
                }
            }
        }
    }

    private static String removeEmojiAndSymbolFromString(String content) {
        String utf8tweet = "";
        try {
            byte[] utf8Bytes = content.getBytes("UTF-8");
            utf8tweet = new String(utf8Bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ExceptionHandler.Handle(ex);
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
    
    
    private static boolean saveAttachment(Part p, String filePath, String filename) {
        try {
            ((MimeBodyPart) p).saveFile(filePath + filename);
            return true;
        } catch (IOException | MessagingException ex) {
            System.err.println("Attachment \"" + filename + "\" could not be saved");
            ExceptionHandler.Handle(ex);
            return false;
        }
    }
    
}
