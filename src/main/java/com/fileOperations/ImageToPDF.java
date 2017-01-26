/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import static com.sun.media.jai.codec.TIFFEncodeParam.COMPRESSION_GROUP4;
import com.util.ExceptionHandler;
import com.util.PDFBoxTools;
import static com.util.PDFBoxTools.TIFFCompression;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author Andrew
 */
public class ImageToPDF {

    /**
     * create PDF from image and scales it accordingly to fit in a single page
     *
     * @param folderPath String
     * @param imageFileName String
     * @return String new filename
     */
    public static String createPDFFromImage(String folderPath, String imageFileName) {
        String pdfFile = FilenameUtils.removeExtension(imageFileName) + ".pdf";
        String image = folderPath + imageFileName;
        PDImageXObject pdImage = null;
        File imageFile = null;
        FileInputStream fileStream = null;
        BufferedImage bim = null;

        // the document
        PDDocument doc = null;
        PDPageContentStream contentStream = null;

        try {
            doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.LETTER);
            float margin = 72;
            float pageWidth = page.getMediaBox().getWidth() - 2 * margin;
            float pageHeight = page.getMediaBox().getHeight() - 2 * margin;

            if (image.toLowerCase().endsWith(".jpg")) {
                imageFile = new File(image);
                fileStream = new FileInputStream(image);
                pdImage = JPEGFactory.createFromStream(doc, fileStream);
            } else if ((image.toLowerCase().endsWith(".tif")
                    || image.toLowerCase().endsWith(".tiff"))
                    && TIFFCompression(image) == COMPRESSION_GROUP4) {
                imageFile = new File(image);
                pdImage = CCITTFactory.createFromFile(doc, imageFile);
            } else if (image.toLowerCase().endsWith(".gif")
                    || image.toLowerCase().endsWith(".bmp")
                    || image.toLowerCase().endsWith(".png")) {
                imageFile = new File(image);
                bim = ImageIO.read(imageFile);
                pdImage = LosslessFactory.createFromImage(doc, bim);
            }

            if (pdImage != null) {
                if (pdImage.getWidth() > pdImage.getHeight()) {
                    page.setRotation(270);
                    PDRectangle rotatedPage = new PDRectangle(page.getMediaBox().getHeight(), page.getMediaBox().getWidth());
                    page.setMediaBox(rotatedPage);
                    pageWidth = page.getMediaBox().getWidth() - 2 * margin;
                    pageHeight = page.getMediaBox().getHeight() - 2 * margin;
                }
                Dimension pageSize = new Dimension((int) pageWidth, (int) pageHeight);
                Dimension imageSize = new Dimension(pdImage.getWidth(), pdImage.getHeight());
                Dimension scaledDim = PDFBoxTools.getScaledDimension(imageSize, pageSize);
                float startX = page.getMediaBox().getLowerLeftX() + margin;
                float startY = page.getMediaBox().getUpperRightY() - margin - scaledDim.height;

                doc.addPage(page);
                contentStream = new PDPageContentStream(doc, page);
                contentStream.drawImage(pdImage, startX, startY, scaledDim.width, scaledDim.height);
                contentStream.close();
                doc.save(folderPath + pdfFile);
            }
        } catch (IOException ex) {
            ExceptionHandler.Handle(ex);
            return "";
        } finally {
            if (doc != null) {
                try {
                    doc.close();

                } catch (IOException ex) {
                    ExceptionHandler.Handle(ex);
                    return "";
                }
            }
        }
        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (IOException ex) {
                ExceptionHandler.Handle(ex);
                return "";
            }
        }
        if (bim != null) {
            bim.flush();
        }
        if (imageFile != null) {
            imageFile.delete();
        }
        return pdfFile;
    }

}
