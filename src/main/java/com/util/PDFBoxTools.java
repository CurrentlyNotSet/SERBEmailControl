/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.sun.media.jai.codec.TIFFDirectory;
import static com.sun.media.jai.codec.TIFFEncodeParam.COMPRESSION_GROUP4;
import com.sun.media.jai.codec.TIFFField;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author Andrew
 */
public class PDFBoxTools {
    
    public static String HeaderTimeStamp(Timestamp docketDate) {
        return Global.getMmddyyyyhhmmssa().format(docketDate);
    }
    
    public static List<String> setLineBreaks(String text, float width, float fontSize, PDFont pdfFont) {
        List<String> lines = new ArrayList<>();
        int lastSpace = -1;

        if (text.contains(System.getProperty("line.separator"))) {
            while (text.length() > 0) {
                try {
                    int spaceIndex = text.indexOf(System.getProperty("line.separator"), lastSpace + 1);
                    if (spaceIndex < 0) {
                        spaceIndex = text.length();
                    }
                    String subString = text.substring(0, spaceIndex);
                    float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
//                System.out.printf("'%s' - %f of %f\n", subString, size, width);
                    if (size > width) {
                        if (lastSpace < 0) {
                            lastSpace = spaceIndex;
                        }
                        subString = text.substring(0, lastSpace);
                        lines.add(subString);
                        text = text.substring(lastSpace).trim();
//                    System.out.printf("'%s' is line\n", subString);
                        lastSpace = -1;
                    } else if (spaceIndex == text.length()) {
                        lines.add(text);
//                    System.out.printf("'%s' is line\n", text);
                        text = "";
                    } else {
                        lastSpace = spaceIndex;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PDFBoxTools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            while (text.length() > 0) {
                try {
                    int spaceIndex = text.indexOf(' ', lastSpace + 1);
                    if (spaceIndex < 0) {
                        spaceIndex = text.length();
                    }
                    String subString = text.substring(0, spaceIndex);
                    float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
//                System.out.printf("'%s' - %f of %f\n", subString, size, width);
                    if (size > width) {
                        if (lastSpace < 0) {
                            lastSpace = spaceIndex;
                        }
                        subString = text.substring(0, lastSpace);
                        lines.add(subString);
                        text = text.substring(lastSpace).trim();
//                    System.out.printf("'%s' is line\n", subString);
                        lastSpace = -1;
                    } else if (spaceIndex == text.length()) {
                        lines.add(text);
//                    System.out.printf("'%s' is line\n", text);
                        text = "";
                    } else {
                        lastSpace = spaceIndex;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PDFBoxTools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return lines;
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    public static PDImageXObject getImage(PDDocument doc, String image) {
        try {
            if (image.toLowerCase().endsWith(".jpg")) {
                return JPEGFactory.createFromStream(doc, new FileInputStream(image));
            } else if ((image.toLowerCase().endsWith(".tif")
                    || image.toLowerCase().endsWith(".tiff"))
                    && TIFFCompression(image) == COMPRESSION_GROUP4) {
                return CCITTFactory.createFromFile(doc, new File(image));
            } else if (image.toLowerCase().endsWith(".gif")
                    || image.toLowerCase().endsWith(".bmp")
                    || image.toLowerCase().endsWith(".png")) {
                BufferedImage bim = ImageIO.read(new File(image));
                return LosslessFactory.createFromImage(doc, bim);
            } else {
                System.out.println("Image type not supported: " + image);
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(PDFBoxTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static int TIFFCompression(String image) {
        // 1 = No compression
        // 2 = CCITT modified Huffman RLE
        // 32773 = PackBits compression, aka Macintosh RLE
        // 3 = CCITT Group 3 fax encoding
        // 4 = CCITT Group 4 fax encoding
        // 5 = LZW
        // 6 = JPEG ('old-style' JPEG, later overriden in Technote2)
        // 7 = JPEG ('new-style' JPEG)
        // 8 = Deflate ('Adobe-style')
        // 9 = Defined by TIFF-F and TIFF-FX standard (RFC 2301) as ITU-T Rec. T.82 coding, using ITU-T Rec. T.85 (which boils down to JBIG on black and white).
        // 10 = Defined by TIFF-F and TIFF-FX standard (RFC 2301) as ITU-T Rec. T.82 coding, using ITU-T Rec. T.43 (which boils down to JBIG on color). 
        
        RenderedOp tiffFile = JAI.create("fileload", image);
        
        int TAG_COMPRESSION = 259;
        TIFFDirectory dir = (TIFFDirectory) tiffFile.getProperty("tiff_directory");
        if (dir.isTagPresent(TAG_COMPRESSION)) {
            TIFFField compField = dir.getField(TAG_COMPRESSION);
            return compField.getAsInt(0);
        }
        return 0;
    }

}
