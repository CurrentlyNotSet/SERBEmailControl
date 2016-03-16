/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.model.EmailMessageModel;
import com.util.Global;
import com.util.PDFBoxTools;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
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

    public static EmailMessageModel createEmailBody(EmailMessageModel emailText) {
        String file = Global.getEmailPath()+ emailText.getSection() 
                + File.separatorChar + "test.pdf";
        
        
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
            List<String> sentDateContent = PDFBoxTools.setLineBreaks(emailText.getSentDate().toString(), width, emailHeaderFontSize, bodyFont);
            List<String> recievedDateContent = PDFBoxTools.setLineBreaks(emailText.getReceivedDate().toString(), width, emailHeaderFontSize, bodyFont);
            List<String> toContent = PDFBoxTools.setLineBreaks(emailText.getEmailTo(), width, emailHeaderFontSize, bodyFont);
            List<String> fromContent = PDFBoxTools.setLineBreaks(emailText.getEmailFrom(), width, emailHeaderFontSize, bodyFont);
            List<String> ccContent = PDFBoxTools.setLineBreaks(emailText.getEmailCC(), width, emailHeaderFontSize, bodyFont);
            List<String> bccContent = PDFBoxTools.setLineBreaks(emailText.getEmailBCC(), width, emailHeaderFontSize, bodyFont);
            List<String> subjectContent = PDFBoxTools.setLineBreaks(emailText.getEmailSubject(), width, emailHeaderFontSize, bodyFont);
            List<String> bodyContent = PDFBoxTools.setLineBreaks(emailText.getEmailBody(), width, bodyFontSize, bodyFont);

            //Set Email Header
            contentStream.beginText();
            contentStream.setFont(bodyFont, emailHeaderFontSize);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(startX, startY);

            //Set Date Sent
            if (emailText.getSentDate() != null || !"".equals(emailText.getSentDate().toString())) {
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
            }
            
            //Set Date Received
            if (emailText.getReceivedDate()!= null || !"".equals(emailText.getReceivedDate().toString())) {
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
            if (emailText.getEmailFrom() != null || !"".equals(emailText.getEmailFrom())) {
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
            if (emailText.getEmailTo() != null || !"".equals(emailText.getEmailTo())) {
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
            if (emailText.getEmailCC() != null || !"".equals(emailText.getEmailCC())) {
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
            if (emailText.getEmailBCC() != null || !"".equals(emailText.getEmailBCC())) {
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
            if (emailText.getEmailSubject() != null || !"".equals(emailText.getEmailSubject())) {
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
            if (emailText.getEmailBody() != null || !"".equals(emailText.getEmailBody())) {
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
            doc.save(file);
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
            emailText.setEmailBodyFileName(file);
        }
        return emailText;
    }
    
}
