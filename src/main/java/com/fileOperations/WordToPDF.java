/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.util.Global;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
 
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author Andrew
 */
public class WordToPDF {
    
    public static String createPDF(String filePath, String fileName) {
        File docxFile = new File(filePath + fileName);
        File pdfFile = new File(filePath + FilenameUtils.removeExtension(fileName) + ".pdf");
        
        try { 
            // 1) Load DOCX into XWPFDocument
            InputStream is = new FileInputStream(docxFile);
            XWPFDocument document = new XWPFDocument(is);
 
            // 2) Prepare Pdf options
            PdfOptions options = PdfOptions.create();
 
            // 3) Convert XWPFDocument to Pdf
            OutputStream out = new FileOutputStream(pdfFile);
            PdfConverter.getInstance().convert(document, out, options);
            
            // 4) Delete the Original .docx
            docxFile.delete();
             
        } catch (IOException | XWPFConverterException e) {
            e.printStackTrace();
        }
        return FilenameUtils.getName(pdfFile.toString());
    }
}
