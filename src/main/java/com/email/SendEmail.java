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
import com.model.ActivityModel;
import com.model.EmailOutAttachmentModel;
import com.model.EmailOutModel;
import com.model.EmailOutRelatedCaseModel;
import com.model.RelatedCaseModel;
import com.model.SECExceptionsModel;
import com.model.SystemEmailModel;
import com.sql.Activity;
import com.sql.Audit;
import com.sql.EmailOut;
import com.sql.EmailOutAttachment;
import com.sql.EmailOutRelatedCase;
import com.sql.RelatedCase;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 *
 * @author Andrew
 */
public class SendEmail {

    /**
     * Sends a single email and uses account based off of the section that the
     * email comes from after email is sent the attachments are gathered
     * together and collated into a single PDF file and a history entry is
     * created based off of that entry
     *
     * @param eml EmailOutModel
     */
    public static void sendEmails(EmailOutModel eml) {
        SystemEmailModel account = null;

        String section = eml.getSection();
        if (eml.getSection().equalsIgnoreCase("Hearings") &&
                (  eml.getCaseType().equalsIgnoreCase("MED") 
                || eml.getCaseType().equalsIgnoreCase("REP")
                || eml.getCaseType().equalsIgnoreCase("ULP"))) {
            section = eml.getCaseType();
        }
                
        //Get Account
        for (SystemEmailModel acc : Global.getSystemEmailParams()) {
            if (acc.getSection().equals(section)) {
                account = acc;
                break;
            }
        }

        //Account Exists?
        if (account != null) {
            //Case Location
            String casePath = (eml.getCaseType().equals("CSC") || eml.getCaseType().equals("ORG"))
                    ? FileService.getCaseFolderORGCSCLocation(eml) : FileService.getCaseFolderLocation(eml);

            //Attachment List
            boolean allFilesExists = true;
            List<EmailOutAttachmentModel> attachmentList = EmailOutAttachment.getAttachmentsByEmail(eml.getId());

            for (EmailOutAttachmentModel attach : attachmentList) {
                File attachment = new File(casePath + attach.getFileName());
                boolean exists = attachment.exists();
                if (exists == false) {
                    allFilesExists = false;
                    SECExceptionsModel item = new SECExceptionsModel();
                    item.setClassName("SendEmail");
                    item.setMethodName("sendEmails");
                    item.setExceptionType("FileMissing");
                    item.setExceptionDescription("Can't Send Email, File Missing for EmailID: " + eml.getId()
                            + System.lineSeparator() + "EmailSubject: " + eml.getSubject()
                            + System.lineSeparator() + "File: " + attachment);

                    ExceptionHandler.HandleNoException(item);

                    break;
                } else {
                    if ("docx".equalsIgnoreCase(FilenameUtils.getExtension(attach.getFileName()))
                            || "doc".equalsIgnoreCase(FilenameUtils.getExtension(attach.getFileName()))) {
                        if (!attachment.renameTo(attachment)) {
                            allFilesExists = false;
                            SECExceptionsModel item = new SECExceptionsModel();
                            item.setClassName("SendEmail");
                            item.setMethodName("sendEmails");
                            item.setExceptionType("File In Use");
                            item.setExceptionDescription("Can't Send Email, File In Use for EmailID: " + eml.getId()
                                    + System.lineSeparator() + "EmailSubject: " + eml.getSubject()
                                    + System.lineSeparator() + "File: " + attachment);

                            ExceptionHandler.HandleNoException(item);
                            break;
                        }
                    }
                }
            }

            if (allFilesExists) {
                //Set up Initial Merge Utility
                PDFMergerUtility ut = new PDFMergerUtility();

                //List ConversionPDFs To Delete Later
                List<String> tempPDFList = new ArrayList<>();

                //create email message body
                Date emailSentTime = new Date();
                String emailPDFname = EmailBodyToPDF.createEmailOutBody(eml, attachmentList, emailSentTime);

                //Add Email Body To PDF Merge
                try {
                    ut.addSource(casePath + emailPDFname);
                    tempPDFList.add(casePath + emailPDFname);
                } catch (FileNotFoundException ex) {
                    ExceptionHandler.Handle(ex);
                }

                //Get parts
                String   FromAddress   = account.getEmailAddress();
                String[] TOAddressess  = ((eml.getTo() == null)  ? "".split(";") : eml.getTo().split(";"));
                String[] CCAddressess  = ((eml.getCc() == null)  ? "".split(";") : eml.getCc().split(";"));
                String[] BCCAddressess = ((eml.getBcc() == null) ? "".split(";") : eml.getBcc().split(";"));
                String   emailSubject  = eml.getSubject();
                String   emailBody     = eml.getBody();

                //Set Email Parts
                Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
                Properties properties = EmailProperties.setEmailOutProperties(account);
                Session session = Session.getInstance(properties, auth);
                MimeMessage smessage = new MimeMessage(session);
                Multipart multipart = new MimeMultipart();

                //Add Parts to Email Message
                try {
                    smessage.addFrom(new InternetAddress[]{new InternetAddress(FromAddress)});
                    for (String To : TOAddressess) {
                        if (EmailValidator.getInstance().isValid(To)) {
                            smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
                        }
                    }
                    for (String CC : CCAddressess) {
                        if (EmailValidator.getInstance().isValid(CC)) {
                            smessage.addRecipient(Message.RecipientType.CC, new InternetAddress(CC));
                        }
                    }
                    for (String BCC : BCCAddressess) {
                        if (EmailValidator.getInstance().isValid(BCC)) {
                            smessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(BCC));
                        }
                    }
                    smessage.setSubject(emailSubject);

                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(emailBody, "text/plain");
                    multipart.addBodyPart(messageBodyPart);

                    //get attachments
                    for (EmailOutAttachmentModel attachment : attachmentList) {
                        String fileName = attachment.getFileName();
                        String extension = FilenameUtils.getExtension(fileName);

                        //Convert attachments to PDF
                        //If Image
                        if (FileService.isImageFormat(fileName)) {
                            fileName = ImageToPDF.createPDFFromImageNoDelete(casePath, fileName);

                            //Add Attachment To PDF Merge
                            try {
                                ut.addSource(casePath + fileName);
                                tempPDFList.add(casePath + fileName);
                            } catch (FileNotFoundException ex) {
                                ExceptionHandler.Handle(ex);
                            }

                            //If Word Doc
                        } else if (extension.equals("docx") || extension.equals("doc")) {
                            fileName = WordToPDF.createPDFNoDelete(casePath, fileName);

                            //Add Attachment To PDF Merge
                            try {
                                ut.addSource(casePath + fileName);
                                tempPDFList.add(casePath + fileName);
                            } catch (FileNotFoundException ex) {
                                ExceptionHandler.Handle(ex);
                            }

                            //If Text File
                        } else if ("txt".equals(extension)) {
                            fileName = TXTtoPDF.createPDFNoDelete(casePath, fileName);

                            //Add Attachment To PDF Merge
                            try {
                                ut.addSource(casePath + fileName);
                                tempPDFList.add(casePath + fileName);
                            } catch (FileNotFoundException ex) {
                                ExceptionHandler.Handle(ex);
                            }

                            //If PDF
                        } else if (FilenameUtils.getExtension(fileName).equals("pdf")) {

                            //Add Attachment To PDF Merge
                            try {
                                ut.addSource(casePath + fileName);
                            } catch (FileNotFoundException ex) {
                                ExceptionHandler.Handle(ex);
                            }
                        }

                        DataSource source = new FileDataSource(casePath + fileName);
                        messageBodyPart = new MimeBodyPart();
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(fileName);
                        multipart.addBodyPart(messageBodyPart);
                    }
                    smessage.setContent(multipart);

                    //Send Message
                    if (Global.isOkToSendEmail()) {
                        Transport.send(smessage);
                    } else {
                        Audit.addAuditEntry("Email Not Actually Sent: " + eml.getId() + " - " + emailSubject);
                    }

                    //DocumentFileName
                    String savedDoc = (String.valueOf(new Date().getTime()) 
                            + "_" + eml.getSubject()).replaceAll("[:\\\\/*?|<>]", "_") + ".pdf";

                    //Set Merge File Destination
                    ut.setDestinationFileName(casePath + savedDoc);

                    //Try to Merge
                    try {
                        ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                    } catch (IOException ex) {
                        ExceptionHandler.Handle(ex);
                    }

                    //Add emailBody Activity
                    addEmailActivity(eml, savedDoc, emailSentTime);

                    //Copy to related case folders for MED
                    if (section.equals("MED")) {
                        List<RelatedCaseModel> relatedMedList = RelatedCase.getRelatedCases(eml);
                        if (relatedMedList.size() > 0) {
                            for (RelatedCaseModel related : relatedMedList) {

                                //Copy finalized document to proper folder
                                File srcFile = new File(casePath + savedDoc);

                                File destPath = new File((section.equals("CSC") || section.equals("ORG"))
                                        ? FileService.getCaseFolderORGCSCLocation(related) : FileService.getCaseFolderLocationRelatedCase(related));
                                destPath.mkdirs();

                                try {
                                    FileUtils.copyFileToDirectory(srcFile, destPath);
                                } catch (IOException ex) {
                                    Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Add Related Case Activity Entry
                                addEmailActivityRelatedCase(eml, related, savedDoc, emailSentTime);
                            }
                        }
                    } else {
                        List<EmailOutRelatedCaseModel> relatedList = EmailOutRelatedCase.getRelatedCases(eml);
                        if (relatedList.size() > 0) {
                            for (EmailOutRelatedCaseModel related : relatedList) {

                                //Copy finalized document to proper folder
                                File srcFile = new File(casePath + savedDoc);

                                File destPath = new File((section.equals("CSC") || section.equals("ORG"))
                                        ? FileService.getCaseFolderORGCSCLocation(related) : FileService.getCaseFolderLocationEmailOutRelatedCase(related));
                                destPath.mkdirs();

                                try {
                                    FileUtils.copyFileToDirectory(srcFile, destPath);
                                } catch (IOException ex) {
                                    Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Add Related Case Activity Entry
                                addEmailOutActivityRelatedCase(eml, related, savedDoc, emailSentTime);
                            }
                        }
                    }

                    //Delete Out entries
                    EmailOut.deleteEmailEntry(eml.getId());
                    EmailOutAttachment.deleteAttachmentsForEmail(eml.getId());
                    //TODO: DELETE related Emails Outs

                    //Clean up temp PDFs
                    for (String tempPDF : tempPDFList) {
                        new File(tempPDF).delete();
                    }

                } catch (AddressException ex) {
                    ExceptionHandler.Handle(ex);
                } catch (MessagingException ex) {
                    ExceptionHandler.Handle(ex);
                }
            }
        }
    }

    /**
     * Adds activity to case after sending email
     *
     * @param eml EmailOutModel
     * @param PDFname String (File Name)
     * @param emailSentTime Date (Time Sent)
     */
    private static void addEmailActivity(EmailOutModel eml, String PDFname, Date emailSentTime) {
        ActivityModel act = new ActivityModel();
        act.setCaseYear(eml.getCaseYear());
        act.setCaseType(eml.getCaseType());
        act.setCaseMonth(eml.getCaseMonth());
        act.setCaseNumber(eml.getCaseNumber());
        act.setUserID(String.valueOf(eml.getUserID()));
        act.setDate(new Timestamp(emailSentTime.getTime()));
        act.setAction(Global.isOkToSendEmail() ? "OUT - " + eml.getSubject() : "OUT (Not Actually Sent) - " + eml.getSubject());
        act.setFileName(PDFname);
        act.setFrom(eml.getFrom());
        act.setTo(eml.getTo());
        act.setType(null);
        act.setComment(null);
        act.setRedacted(0);
        act.setAwaitingTimestamp(0);

        //Insert Email
        Activity.insertActivity(act);
    }

    /**
     * Adds activity to related case after sending email
     *
     * @param eml EmailOutModel
     * @param PDFname String (File Name)
     * @param emailSentTime Date (Time Sent)
     */
    private static void addEmailActivityRelatedCase(EmailOutModel eml, RelatedCaseModel related, String PDFname, Date emailSentTime) {
        ActivityModel act = new ActivityModel();
        act.setCaseYear(related.getRelatedCaseYear());
        act.setCaseType(related.getRelatedCaseType());
        act.setCaseMonth(related.getRelatedCaseMonth());
        act.setCaseNumber(related.getRelatedCaseNumber());
        act.setUserID(String.valueOf(eml.getUserID()));
        act.setDate(new Timestamp(emailSentTime.getTime()));
        act.setAction(Global.isOkToSendEmail() ? "OUT - " + eml.getSubject() : "OUT (Not Actually Sent) - " + eml.getSubject());
        act.setFileName(PDFname);
        act.setFrom(eml.getFrom());
        act.setTo(eml.getTo());
        act.setType("");
        act.setComment(eml.getBody());
        act.setRedacted(0);
        act.setAwaitingTimestamp(0);

        //Insert Email
        Activity.insertActivity(act);
    }

    /**
     * Adds activity to related case after sending email
     *
     * @param eml EmailOutModel
     * @param PDFname String (File Name)
     * @param emailSentTime Date (Time Sent)
     */
    private static void addEmailOutActivityRelatedCase(EmailOutModel eml, EmailOutRelatedCaseModel related, String PDFname, Date emailSentTime) {
        ActivityModel act = new ActivityModel();
        act.setCaseYear(related.getCaseYear());
        act.setCaseType(related.getCaseType());
        act.setCaseMonth(related.getCaseMonth());
        act.setCaseNumber(related.getCaseNumber());
        act.setUserID(String.valueOf(eml.getUserID()));
        act.setDate(new Timestamp(emailSentTime.getTime()));
        act.setAction(Global.isOkToSendEmail() ? "OUT - " + eml.getSubject() : "OUT (Not Actually Sent) - " + eml.getSubject());
        act.setFileName(PDFname);
        act.setFrom(eml.getFrom());
        act.setTo(eml.getTo());
        act.setType("");
        act.setComment(eml.getBody());
        act.setRedacted(0);
        act.setAwaitingTimestamp(0);

        //Insert Email
        Activity.insertActivity(act);
    }
}
