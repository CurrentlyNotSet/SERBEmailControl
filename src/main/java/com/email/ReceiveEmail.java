/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.fileOperations.EmailBodyToPDF;
import com.fileOperations.ImageToPDF;
import com.fileOperations.StampPDF;
import com.fileOperations.TXTtoPDF;
import com.fileOperations.WordToPDF;
import com.model.EmailMessageModel;
import com.model.SECExceptionsModel;
import com.model.SystemEmailModel;
import com.sql.EMail;
import com.sql.EmailAttachment;
import com.sql.SECExceptions;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import com.util.SlackNotification;
import com.util.StringUtilities;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
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
    private static final boolean deleteEmailEnabled = true;

    /**
     * This account fetches emails from a specified account and marks the emails
     * as seen, only touches the email account does not delete or move any
     * information.
     *
     * @param account SystemEmailModel
     */
    public static void fetchEmail(SystemEmailModel account) {
        Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
        Properties properties = EmailProperties.setEmailInProperties(account);

        try {
            Session session = Session.getInstance(properties, auth);
            Store store = session.getStore();
            store.connect(account.getIncomingURL(), account.getIncomingPort(), account.getUsername(), account.getPassword());
            Folder fetchFolder = store.getFolder(account.getIncomingFolder());
            if (account.getIncomingFolder().trim().equals("")) {
                fetchFolder = store.getFolder("INBOX");
            }

            if (fetchFolder.exists()) {
                fetchFolder.open(Folder.READ_WRITE);
                Message[] msgs = fetchFolder.getMessages();

                // USE THIS FOR UNSEEN MAIL TO SEEN MAIL
                //Flags seen = new Flags(Flags.Flag.SEEN);
                //FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
                //Message[] msgs = fetchFolder.search(unseenFlagTerm);
                //fetchFolder.setFlags(msgs, seen, true);
                if (msgs.length > 0) {
                    List<String> messageList = new ArrayList<>();

                    for (Message msg : msgs) {
                        boolean notDuplicate = true;
                        String headerText = Arrays.toString(msg.getHeader("Message-ID"));

                        for (String header : messageList) {
                            if (header.equals(headerText)) {
                                notDuplicate = false;
                                break;
                            }
                        }

                        if (notDuplicate) {
                            //Add to header to stop duplicates
                            messageList.add(headerText);
                            attachmentCount = 1;
                            attachmentList = new ArrayList<>();

                            //Setup Email For dbo.Email
                            EmailMessageModel eml = new EmailMessageModel();
                            String emailTime = String.valueOf(new Date().getTime());
                            eml.setSection(account.getSection());
                            eml = saveEnvelope(msg, msg, eml);
                            eml.setId(EMail.InsertEmail(eml));

                            //After Email has been inserted Gather Attachments
                            saveAttachments(msg, msg, eml);

                            //Create Email Body
                            eml = EmailBodyToPDF.createEmailBodyIn(eml, emailTime, attachmentList);

                            //Insert Email Body As Attachment (so it shows up in attachment table during Docketing)
                            EmailAttachment.insertEmailAttachment(eml.getId(), eml.getEmailBodyFileName());

                            //Flag email As ready to file so it is available in the docket tab of SERB3.0
                            eml.setReadyToFile(1);
                            EMail.setEmailReadyToFile(eml);
                        }

                        if (deleteEmailEnabled) {
                            //  Will Delete message from server
                            //  Un Comment line below to run
                            msg.setFlag(Flags.Flag.DELETED, true);
                        }
                    }
                }
                fetchFolder.close(true);
                store.close();
            }
        } catch (MessagingException ex) {
            if (ex != null) {
                System.out.println("Unable to connect to email Server for: "
                        + account.getEmailAddress()
                        + "\nPlease ensure you are connected to the network and"
                        + " try again.");
            }
            ExceptionHandler.Handle(ex);
        }
    }

    /**
     * Saved the list of all of the TO, FROM, CC, BCC, and dates
     *
     * @param m Message
     * @param p Part
     * @param eml EmailMessageModel
     * @return EmailMessageModel
     */
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

            //Get email body
            String emailBody = getEmailBodyText(p);
            
            // clean email Body
            emailBody = StringUtilities.replaceOfficeTags(emailBody); 
            
            if (StringUtilities.isHtml(emailBody)) {
                Source htmlSource = new Source(emailBody);
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

    /**
     * Gather the email body
     *
     * @param p Part
     * @return String body
     */
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

    /**
     * Save the attachments from the email
     *
     * @param p Part
     * @param m Message
     * @param eml EmailMessageModel
     */
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

    /**
     * Save the actual attachment and convert the file if needed
     *
     * @param part Part
     * @param filename String
     * @param eml EmailMessageModel
     */
    private static void saveFile(Part part, String filename, EmailMessageModel eml) {
        int i = attachmentCount++;
        String filePath = Global.getEmailPath() + eml.getSection() + File.separatorChar;
        if (FileService.isValidAttachment(filename)) {
            String extension = FilenameUtils.getExtension(filename);
            String fileNameDB = StringUtilities.properAttachmentName(filename, eml.getId(), i);

            if (saveAttachment(part, filePath, fileNameDB)) {
                if (FileService.isImageFormat(filename)) {
                    fileNameDB = ImageToPDF.createPDFFromImage(filePath, fileNameDB);
                } else if ("docx".equalsIgnoreCase(extension) || "doc".equalsIgnoreCase(extension)) {
                    fileNameDB = WordToPDF.createPDF(filePath, fileNameDB);
                } else if ("txt".equalsIgnoreCase(extension)) {
                    fileNameDB = TXTtoPDF.createPDF(filePath, fileNameDB);
                }
                if (!"".equals(fileNameDB)) {
                    if (FilenameUtils.getExtension(fileNameDB).equalsIgnoreCase("pdf")) {
                        StampPDF.stampDocument(filePath + fileNameDB, eml.getReceivedDate(), StringUtilities.getDepartmentByCaseType(eml.getSection()));
                    }
                    attachmentList.add(fileNameDB);
                    EmailAttachment.insertEmailAttachment(eml.getId(), fileNameDB);
                } else {
                    SECExceptionsModel item = new SECExceptionsModel();
                    item.setClassName("com.email.ReceiveEmail");
                    item.setMethodName("saveFile");
                    item.setExceptionType("AttachmentNotSaved");
                    item.setExceptionDescription("Conversion Error: " + fileNameDB + "Could not be saved to the Database");

                    //Print out to commandline
                    Logger.getLogger("Conversion Error: " + fileNameDB + "Could not be saved to the Database");

                    //Send to the Server
                    if (SECExceptions.insertException(item)) {
                        //true = failed out || send to Slack instead
                        SlackNotification.sendNotification("Conversion Error: " + fileNameDB + "Could not be saved to the Database");
                    }
                }
            }
        }
    }

    /**
     * Strip out the emojis and symbols from the email so we can actually save
     * it in the database
     *
     * @param content String
     * @return String
     */
    private static String removeEmojiAndSymbolFromString(String content) {
        String utf8tweet = "";

        if (content != null) {
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
        }

        return utf8tweet;
    }

    /**
     * Save the attachment to the drive
     *
     * @param p Part
     * @param filePath String
     * @param filename String
     * @return boolean
     */
    private static boolean saveAttachment(Part p, String filePath, String filename) {
        File attachmentLocation = new File(filePath);
        if (!attachmentLocation.exists()) {
            attachmentLocation.mkdirs();
        }

        try {
            ((MimeBodyPart) p).saveFile(filePath + filename);
            return true;
        } catch (IOException ex) {
            System.err.println("Attachment \"" + filename + "\" could not be saved: IOException");
            ExceptionHandler.Handle(ex);
        } catch (MessagingException ex) {
            System.err.println("Attachment \"" + filename + "\" could not be saved: MessagingException");
            ExceptionHandler.Handle(ex);
        }
        return false;
    }

}
