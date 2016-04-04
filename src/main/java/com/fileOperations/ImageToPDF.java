/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import static com.sun.media.jai.codec.TIFFEncodeParam.COMPRESSION_GROUP4;
import com.util.PDFBoxTools;
import static com.util.PDFBoxTools.TIFFCompression;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
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
     * create the second sample document from the PDF file format specification.
     * @param folderPath
     * @param imageFileName
     * @return 
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
            PDPage page = new PDPage();
            float margin = 72;
            float pageWidth = page.getMediaBox().getWidth() - 2 * margin;
            float pageHeight = page.getMediaBox().getHeight() - 2 * margin;

            if (image.toLowerCase().endsWith(".jpg")) {
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
            Logger.getLogger(ImageToPDF.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                    if (fileStream != null){
                        fileStream.close();
                    }
                    if (bim != null){
                        bim.flush();
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(ImageToPDF.class.getName()).log(Level.SEVERE, null, ex);
                    return "";
                }
            }
        }
        return pdfFile;
    }

}
