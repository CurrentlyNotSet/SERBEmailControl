/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.util.PDFBoxTools;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author Andrew
 */
public class ImageToPDF {

    /**
     * create the second sample document from the PDF file format specification.
     * @param folderPath
     * @param imageFile
     */
    public static String createPDFFromImage(String folderPath, String imageFile) {
        // the document
        PDDocument doc = null;
        PDPageContentStream contentStream = null;
        
        try {
            doc = new PDDocument();
            PDPage page = new PDPage();
            float margin = 72;
            float pageWidth = page.getMediaBox().getWidth() - 2 * margin;
            float pageHeight = page.getMediaBox().getWidth() - 2 * margin;
            float startX = page.getMediaBox().getLowerLeftX() + margin;
            float startY = page.getMediaBox().getUpperRightY() - margin;
            

            PDImageXObject pdImage = PDFBoxTools.getImage(doc, folderPath + imageFile);
            if (pdImage != null) {
                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page);

                Dimension scaledDim = PDFBoxTools.getScaledDimension(
                        new Dimension(pdImage.getWidth(), pdImage.getHeight()),
                        new Dimension((int) pageWidth, (int) pageHeight));
                contentStream.drawImage(pdImage, startX, scaledDim.height + margin, scaledDim.width, scaledDim.height);

                contentStream.close();
                doc.save(folderPath + FilenameUtils.removeExtension(imageFile) + ".pdf");
            }
        } catch (IOException ex) {
            Logger.getLogger(ImageToPDF.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageToPDF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File image = new File(folderPath + imageFile);
            image.delete();
        }
        return FilenameUtils.removeExtension(imageFile) + ".pdf";
    }

}
