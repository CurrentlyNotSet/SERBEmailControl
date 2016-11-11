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
import com.model.SystemEmailModel;
import com.sql.Activity;
import com.sql.EmailOut;
import com.sql.EmailOutAttachment;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 *
 * @author Andrew
 */
public class SendEmail {

    public static void sendEmails(EmailOutModel eml) {
        SystemEmailModel account = null;
        
        //Get Account
        for (SystemEmailModel acc : Global.getSystemEmailParams()) {
            if (acc.getSection().equals(eml.getSection())) {
                account = acc;
                break;
            }
        }
        
        //Account Exists?
        if (account != null) {
            //Case Location
            String casePath = FileService.getCaseFolderLocation(eml);
            
            //Attachment List
            List<EmailOutAttachmentModel> attachmentList = EmailOutAttachment.getAttachmentsByEmail(eml.getId());

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
            String FromAddress = account.getEmailAddress();
            String[] TOAddressess = ((eml.getTo() == null) ? "".split(";") : eml.getTo().split(";"));
            String[] CCAddressess = ((eml.getCc() == null) ? "".split(";") : eml.getCc().split(";"));
            String[] BCCAddressess = ((eml.getBcc()== null) ? "".split(";") : eml.getBcc().split(";"));
            String emailSubject = eml.getSubject();
            String emailBody = eml.getBody();

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
                messageBodyPart.setContent(emailBody, "text/html");      
                multipart.addBodyPart(messageBodyPart);
                
                //get attachments
                for (EmailOutAttachmentModel attachment : attachmentList) {
                    String fileName = attachment.getFileName();
                    String extension = FilenameUtils.getExtension(fileName);

                    //Convert attachments to PDF
                    
                    //If Image
                    if (FileService.isImageFormat(fileName)) {
                        fileName = ImageToPDF.createPDFFromImage(casePath, fileName);

                        //Add Attachment To PDF Merge
                        try {
                            ut.addSource(casePath + fileName);
                            tempPDFList.add(casePath + fileName);
                        } catch (FileNotFoundException ex) {
                            ExceptionHandler.Handle(ex);
                        }

                    //If Word Doc
                    } else if (extension.equals("docx") || extension.equals("doc")) {
                        fileName = WordToPDF.createPDF(casePath, fileName);

                        //Add Attachment To PDF Merge
                        try {
                            ut.addSource(casePath + fileName);
                            tempPDFList.add(casePath + fileName);
                        } catch (FileNotFoundException ex) {
                            ExceptionHandler.Handle(ex);
                        }
                        
                    //If Text File
                    } else if ("txt".equals(extension)) {
                        fileName = TXTtoPDF.createPDF(casePath, fileName);

                        //Add Attachment To PDF Merge
                        try {
                            ut.addSource(casePath + fileName);
                            tempPDFList.add(casePath + fileName);
                        } catch (FileNotFoundException ex) {
                            ExceptionHandler.Handle(ex);
                        }

                    //If PDF
                    } else if (FilenameUtils.getExtension(fileName).equals(".pdf")) {

                        //Add Attachment To PDF Merge
                        try {
                            ut.addSource(casePath + fileName);
                        } catch (FileNotFoundException ex) {
                            ExceptionHandler.Handle(ex);
                        }
                    }
                    
                    DataSource source = new FileDataSource(fileName);
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);
                }
                smessage.setContent(multipart);
                
                //Send Message
                Transport.send(smessage);
                
                //DocumentFileName
                String savedDoc = String.valueOf(new Date().getTime()) + "_" + eml.getSubject() + ".pdf";

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

                //Delete Out entries
                EmailOut.deleteEmailEntry(eml.getId());
                EmailOutAttachment.deleteAttachmentsForEmail(eml.getId());

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

    private static void addEmailActivity(EmailOutModel eml, String PDFname, Date emailSentTime) {
        ActivityModel act = new ActivityModel();
        act.setCaseYear(eml.getCaseYear());
        act.setCaseType(eml.getCaseType());
        act.setCaseMonth(eml.getCaseMonth());
        act.setCaseNumber(eml.getCaseNumber());
        act.setUserID(String.valueOf(eml.getUserID()));
        act.setDate(new Timestamp(emailSentTime.getTime()));
        act.setAction("OUT - " + eml.getSubject());
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
