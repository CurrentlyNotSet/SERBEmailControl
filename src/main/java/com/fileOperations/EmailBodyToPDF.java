/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.model.EmailMessageModel;
import com.model.EmailOutAttachmentModel;
import com.model.EmailOutModel;
import com.util.FileService;
import com.util.Global;
import com.util.PDFBoxTools;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Andrew
 */
public class EmailBodyToPDF {

    public static EmailMessageModel createEmailBody(EmailMessageModel eml, String emailTime) {
        String filePath = Global.getEmailPath()+ eml.getSection() 
                + File.separatorChar ;
        String fileName = eml.getId() + "_" + emailTime + ".pdf";
        
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
            contentStream = new PDPageContentStream(doc, page, true, true, false);
            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;
            float width = mediabox.getWidth() - 2 * margin;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;
            float textYlocation = margin;
            
            //Set Line Breaks
//            List<String> sentDateContent = PDFBoxTools.setLineBreaks(Global.getMmddyyyyhhmmssa().format(emailText.getSentDate()), width, emailHeaderFontSize, bodyFont);
            List<String> recievedDateContent = PDFBoxTools.setLineBreaks(Global.getMmddyyyyhhmmssa().format(eml.getReceivedDate()), width, emailHeaderFontSize, bodyFont);
            List<String> toContent = PDFBoxTools.setLineBreaks(eml.getEmailTo(), width, emailHeaderFontSize, bodyFont);
            List<String> fromContent = PDFBoxTools.setLineBreaks(eml.getEmailFrom(), width, emailHeaderFontSize, bodyFont);
            List<String> ccContent = PDFBoxTools.setLineBreaks(eml.getEmailCC(), width, emailHeaderFontSize, bodyFont);
            List<String> bccContent = PDFBoxTools.setLineBreaks(eml.getEmailBCC(), width, emailHeaderFontSize, bodyFont);
            List<String> subjectContent = PDFBoxTools.setLineBreaks(eml.getEmailSubject(), width, emailHeaderFontSize, bodyFont);
            List<String> bodyContent = PDFBoxTools.setLineBreaks(eml.getEmailBody(), width, bodyFontSize, bodyFont);

            //Set Email Header
            contentStream.beginText();
            contentStream.setFont(bodyFont, emailHeaderFontSize);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(startX, startY);

//            //Set Date Sent
//            if (emailText.getSentDate() != null || !"".equals(emailText.getSentDate().toString())) {
//                contentStream.setFont(bodyTitleFont, emailHeaderFontSize);
//                contentStream.showText("Date Sent: ");
//                contentStream.setFont(bodyFont, emailHeaderFontSize);
//                contentStream.newLineAtOffset(0, -leadingEmailHeader);
//                textYlocation += leadingEmailHeader;
//                for (String line : sentDateContent) {
//                    if (textYlocation > (mediabox.getHeight() - (margin * 2) - leadingEmailHeader)) {
//                        contentStream.endText();
//                        contentStream.close();
//                        textYlocation = 0;
//
//                        page = new PDPage();
//                        doc.addPage(page);
//                        contentStream = new PDPageContentStream(doc, page, true, true, false);
//
//                        contentStream.beginText();
//                        contentStream.setFont(bodyFont, emailHeaderFontSize);
//                        contentStream.setNonStrokingColor(Color.BLACK);
//                        contentStream.newLineAtOffset(startX, startY);
//                    }
//
//                    contentStream.showText(line);
//                    contentStream.newLineAtOffset(0, -leadingEmailHeader);
//                    textYlocation += leadingEmailHeader;
//                }
//            }
            
            //Set Date Received
            if (eml.getReceivedDate()!= null || !"".equals(eml.getReceivedDate().toString().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailFrom().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailTo().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailCC().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailBCC().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailSubject().trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(eml.getEmailBody().trim())) {
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
                    contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            doc.save(filePath + fileName);
        } catch (IOException ex) {
            Logger.getLogger(EmailBodyToPDF.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ex) {
                    Logger.getLogger(EmailBodyToPDF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            eml.setEmailBodyFileName(fileName);
        }
        return eml;
    }

    public static String emailOutBody(EmailOutModel eml, List<EmailOutAttachmentModel> attachmentList, Date emailSentTime){
        String filePath = FileService.getCaseFolderLocation(eml);
        String fileName = String.valueOf(emailSentTime.getTime()) + ".pdf";
        String attachList = "";
        
        for (EmailOutAttachmentModel attachment : attachmentList){
            if (!"".equals(attachList)){
                attachList += "; " + attachment;
            } else{
                attachList += attachment;
            }
        }
        
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
            contentStream = new PDPageContentStream(doc, page, true, true, false);
            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;
            float width = mediabox.getWidth() - 2 * margin;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;
            float textYlocation = margin;
            
            //Set Line Breaks
            List<String> sentDateContent = PDFBoxTools.setLineBreaks(Global.getMmddyyyyhhmmssa().format(emailSentTime), width, emailHeaderFontSize, bodyFont);
            List<String> toContent = PDFBoxTools.setLineBreaks(eml.getTo(), width, emailHeaderFontSize, bodyFont);
            List<String> fromContent = PDFBoxTools.setLineBreaks(eml.getFrom(), width, emailHeaderFontSize, bodyFont);
            List<String> ccContent = PDFBoxTools.setLineBreaks(eml.getCc(), width, emailHeaderFontSize, bodyFont);
            List<String> bccContent = PDFBoxTools.setLineBreaks(eml.getBcc(), width, emailHeaderFontSize, bodyFont);
            List<String> attachmentContent = PDFBoxTools.setLineBreaks(attachList, width, emailHeaderFontSize, bodyFont);
            List<String> subjectContent = PDFBoxTools.setLineBreaks(eml.getSubject(), width, emailHeaderFontSize, bodyFont);
            List<String> bodyContent = PDFBoxTools.setLineBreaks(eml.getBody(), width, bodyFontSize, bodyFont);

            //Set Email Header
            contentStream.beginText();
            contentStream.setFont(bodyFont, emailHeaderFontSize);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(startX, startY);

            //Set Date Sent
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
                    contentStream = new PDPageContentStream(doc, page, true, true, false);

                    contentStream.beginText();
                    contentStream.setFont(bodyFont, emailHeaderFontSize);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.newLineAtOffset(startX, startY);
                }

                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leadingEmailHeader);
                textYlocation += leadingEmailHeader;
            }

            
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            if (!"".equals(attachList.trim())) {
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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
                        contentStream = new PDPageContentStream(doc, page, true, true, false);

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
                        
            // Set Email Body
            if (!"".equals(eml.getBody().trim())) {
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
                    contentStream = new PDPageContentStream(doc, page, true, true, false);

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
            doc.save(filePath + fileName);
        } catch (IOException ex) {
            Logger.getLogger(EmailBodyToPDF.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ex) {
                    Logger.getLogger(EmailBodyToPDF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return fileName;
    }
    
    
}
