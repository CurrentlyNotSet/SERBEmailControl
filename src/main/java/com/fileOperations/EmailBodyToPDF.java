/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.model.EmailBodyPDF;
import com.model.EmailMessageModel;
import com.model.EmailOutAttachmentModel;
import com.model.EmailOutModel;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import com.util.PDFBoxTools;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Andrew
 */
public class EmailBodyToPDF {

    public static EmailMessageModel createEmailBodyIn(EmailMessageModel eml, String emailTime, List<String> attachmentList){
        String filePath = Global.getEmailPath()+ eml.getSection() 
                + File.separatorChar ;
        String fileName = eml.getId() + "_00_" + emailTime + ".pdf";
        String attachList = "";
        for (String attachment : attachmentList){
            if ("".equals(attachList)) {
                attachList += attachment;
            } else {
                attachList += "; " + attachment;
            }
        }
        
        eml.setEmailBodyFileName(fileName);
        EmailBodyPDF emlPDF = new EmailBodyPDF();
        emlPDF.setFilePath(filePath);
        emlPDF.setFileName(fileName);
        emlPDF.setSentDate("");
        emlPDF.setReceiveDate(Global.getMmddyyyyhhmmssa().format(eml.getReceivedDate()));
        emlPDF.setTo(eml.getEmailTo() == null ? "" : eml.getEmailTo());
        emlPDF.setFrom(eml.getEmailFrom() == null ? "" : eml.getEmailFrom());
        emlPDF.setCc(eml.getEmailCC() == null ? "" : eml.getEmailCC());
        emlPDF.setBcc(eml.getEmailBCC() == null ? "" : eml.getEmailBCC());
        emlPDF.setAttachments(attachList);
        emlPDF.setSubject(eml.getEmailSubject() == null ? "" : eml.getEmailSubject());
        emlPDF.setBody(eml.getEmailBody() == null ? "" : eml.getEmailBody());
        
        createEmailBody(emlPDF);
        return eml;
    }
    
    public static String createEmailOutBody(EmailOutModel eml, List<EmailOutAttachmentModel> attachmentList, Date emailSentTime) {
        String filePath = (eml.getCaseType().equals("CSC") || eml.getCaseType().equals("ORG")) 
                ? FileService.getCaseFolderORGCSCLocation(eml) : FileService.getCaseFolderLocation(eml);
        String fileName = String.valueOf(emailSentTime.getTime()) + ".pdf";
        String attachList = "";
        
        for (EmailOutAttachmentModel attachment : attachmentList){
            if (!"".equals(attachList)){
                attachList += "; " + attachment;
            } else{
                attachList += attachment;
            }
        }
                
        EmailBodyPDF emlPDF = new EmailBodyPDF();
        emlPDF.setFilePath(filePath);
        emlPDF.setFileName(fileName);
        emlPDF.setSentDate(Global.getMmddyyyyhhmmssa().format(emailSentTime));
        emlPDF.setReceiveDate("");
        emlPDF.setTo(eml.getTo() == null ? "" : eml.getTo());
        emlPDF.setFrom(eml.getFrom() == null ? "" : eml.getFrom());
        emlPDF.setCc(eml.getCc() == null ? "" : eml.getCc());
        emlPDF.setBcc(eml.getBcc() == null ? "" : eml.getBcc());
        emlPDF.setAttachments(attachList);
        emlPDF.setSubject(eml.getSubject() == null ? "" : eml.getSubject());
        emlPDF.setBody(eml.getBody() == null ? "" : eml.getBody());
        
        createEmailBody(emlPDF);
        return fileName;
    }
    
    private static void createEmailBody(EmailBodyPDF eml) {      
        PDDocument doc = null;
        PDPageContentStream contentStream = null;
        
        //Fonts used
        PDFont bodyTitleFont = PDType1Font.HELVETICA_BOLD;
        PDFont bodyFont = PDType1Font.HELVETICA;
        
        //Font Sizes
        float emailHeaderFontSize = 7;
        float leadingEmailHeader = 1.5f * emailHeaderFontSize;
        float bodyFontSize = 12;
        float leadingBody = 1.5f * bodyFontSize;

        try {            
            //Create Document, Page, Margins.
            doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);
            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;
            float width = mediabox.getWidth() - 2 * margin;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;
            float textYlocation = margin;
            
            //Set Line Breaks
            List<String> sentDateContent = PDFBoxTools.setLineBreaks(eml.getSentDate(), width, emailHeaderFontSize, bodyFont);
            List<String> recievedDateContent = PDFBoxTools.setLineBreaks(eml.getReceiveDate(), width, emailHeaderFontSize, bodyFont);
            List<String> toContent = PDFBoxTools.setLineBreaks(eml.getTo(), width, emailHeaderFontSize, bodyFont);
            List<String> fromContent = PDFBoxTools.setLineBreaks(eml.getFrom(), width, emailHeaderFontSize, bodyFont);
            List<String> ccContent = PDFBoxTools.setLineBreaks(eml.getCc(), width, emailHeaderFontSize, bodyFont);
            List<String> bccContent = PDFBoxTools.setLineBreaks(eml.getBcc(), width, emailHeaderFontSize, bodyFont);
            List<String> attachmentContent = PDFBoxTools.setLineBreaks(eml.getAttachments(), width, emailHeaderFontSize, bodyFont);
            List<String> subjectContent = PDFBoxTools.setLineBreaks(eml.getSubject(), width, emailHeaderFontSize, bodyFont);
            List<String> bodyContent = PDFBoxTools.setLineBreaks(eml.getBody(), width, bodyFontSize, bodyFont);

            //Set Email Header
            contentStream.beginText();
            contentStream.setFont(bodyFont, emailHeaderFontSize);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(startX, startY);

            //Set Date Sent
            if (!"".equals(eml.getSentDate())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("Date Sent: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : sentDateContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }

                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set Date Received
            if (!"".equals(eml.getReceiveDate().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("Date Received: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : recievedDateContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }

                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingBody;
                }
            }
            contentStream.newLineAtOffset(0, -leadingBody);
            
            //Set From
            if (!"".equals(eml.getFrom().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("From: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : fromContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set To
            if (!"".equals(eml.getTo().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("To: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : toContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set CC
            if (!"".equals(eml.getCc().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("CC: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : ccContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set BCC
            if (!"".equals(eml.getBcc().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("BCC: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : bccContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set AttachmentList
            if (!"".equals(eml.getAttachments().trim())) {
                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
                contentStream.showText("Attachments: ");
                contentStream.setFont(bodyFont, emailHeaderFontSize);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
                for (String line : attachmentContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
                    textYlocation += leadingEmailHeader;
                }
            }
            
            //Set Subject
            if (!"".equals(eml.getSubject().trim())) {
                contentStream.newLineAtOffset(0, -leadingBody);
                contentStream.newLineAtOffset(0, -leadingBody);
                contentStream.setFont(bodyTitleFont, bodyFontSize);
                contentStream.showText("Subject: ");
                contentStream.newLineAtOffset(0, -leadingBody);
                textYlocation += leadingBody;
                contentStream.setFont(bodyFont, bodyFontSize);
                for (String line : subjectContent) {
                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
                        contentStream.endText();
                        contentStream.close();
                        textYlocation = 0;

                        page = new PDPage();
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, emailHeaderFontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leadingBody);
                    textYlocation += leadingBody;
                }
            }
            if (!"".equals(eml.getBody().trim())) {
            // Set Email Body
            contentStream.newLineAtOffset(0, -leadingBody);
            contentStream.setFont(bodyTitleFont, bodyFontSize);
            contentStream.showText("Message: ");
            contentStream.setFont(bodyFont, bodyFontSize);
            contentStream.newLineAtOffset(0, -leadingBody);
            for (String line : bodyContent) {
                if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingBody)) {
                    contentStream.endText();
                    contentStream.close();
                    textYlocation = 0;

                    page = new PDPage();
                    doc.addPage(page);
                    contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, false);

                    contentStream.beginText();
                    contentStream.setFont(bodyFont, bodyFontSize);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.newLineAtOffset(startX, startY);
                }

                textYlocation += leadingBody;

                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leadingBody);
            }
            contentStream.endText();
            
            }
            contentStream.close();
            doc.save(eml.getFilePath() + eml.getFileName());
        } catch (IOException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ex) {
                    ExceptionHandler.Handle(ex);
                }
            }
        }
    }
    
}
