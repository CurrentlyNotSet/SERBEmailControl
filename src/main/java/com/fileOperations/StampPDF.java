/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.util.ExceptionHandler;
import com.util.PDFBoxTools;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Andrew
 */
public class StampPDF {

    /**
     * This stamps docketed files.
     *
     * @param file String (full file path)
     * @param docketTime Timestamp
     */
    public static void stampDocument(String file, Timestamp docketTime) {
        // the document
        PDDocument doc = null;
        try {
            PDFont stampFont = PDType1Font.TIMES_ROMAN;
            float stampFontSize = 10;
            String title = PDFBoxTools.HeaderTimeStamp(docketTime);
            float titleWidth = stampFont.getStringWidth(title) / 1000 * stampFontSize;
            float titleHeight = stampFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * stampFontSize;
            int marginTop = 20;

            doc = PDDocument.load(new File(file));

            for (int i = 0; i < doc.getPages().getCount(); i++) {
                PDPageContentStream contentStream = null;

                PDPage page = (PDPage) doc.getPages().get(i);

                contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
                page.getResources().getFontNames();

                contentStream.beginText();
                contentStream.setFont(stampFont, stampFontSize);
                contentStream.setNonStrokingColor(Color.PINK);
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, page.getMediaBox().getHeight() - marginTop - titleHeight);
                contentStream.showText(title);
                contentStream.endText();

                contentStream.close();
            }
            doc.save(file);
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
