/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.util.ExceptionHandler;
import com.util.PDFBoxTools;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
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
public class TXTtoPDF {

    /**
     * creates PDF from text document
     *
     * @param filePath String
     * @param fileName String
     * @return String - new File Name
     */
    public static String createPDF(String filePath, String fileName) {
        String txtFile = filePath + fileName;
        String pdfFile = filePath + FilenameUtils.removeExtension(fileName) + ".pdf";

        File attachmentLocation = new File(filePath);
        if (!attachmentLocation.exists()) {
            attachmentLocation.mkdirs();
        }

        makePDF(pdfFile, getTextfromTXT(txtFile));

        new File(txtFile).delete();

        return FilenameUtils.getName(pdfFile);
    }

    /**
     * creates PDF from text document
     *
     * @param filePath String
     * @param fileName String
     * @return String - new File Name
     */
    public static String createPDFNoDelete(String filePath, String fileName) {
        String txtFile = filePath + fileName;
        String pdfFile = filePath + FilenameUtils.removeExtension(fileName) + ".pdf";

        File attachmentLocation = new File(filePath);
        if (!attachmentLocation.exists()) {
            attachmentLocation.mkdirs();
        }

        makePDF(pdfFile, getTextfromTXT(txtFile));
        return FilenameUtils.getName(pdfFile);
    }

    /**
     * Read the text from the .TXT document
     *
     * @param file String (path + filename)
     * @return String (text from document)
     */
    private static String getTextfromTXT(String file) {
        String textBody = "";
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = br.readLine()) != null) {
                textBody += sCurrentLine + System.getProperty("line.separator");
            }
        } catch (IOException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ExceptionHandler.Handle(ex);
            }
        }
        return textBody;
    }

    /**
     * Takes the text from the string and insert it into the PDF file
     *
     * @param pdfFile String (path + filename)
     * @param text String (text from document)
     */
    private static void makePDF(String pdfFile, String text) {
        PDDocument doc = null;
        PDPageContentStream contentStream = null;

        //Fonts used
        PDFont bodyFont = PDType1Font.TIMES_ROMAN;

        //Font Sizes
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
            text = text.replaceAll("[\\p{C}\\p{Z}]", System.getProperty("line.separator")); //strip ZERO WIDTH SPACE
            List<String> textContent = PDFBoxTools.setLineBreaks(text, width, bodyFontSize, bodyFont);

            contentStream.beginText();
            contentStream.setFont(bodyFont, bodyFontSize);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(startX, startY);

            if (!"".equals(text)) {
                for (String line : textContent) {
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
            doc.save(pdfFile);
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
