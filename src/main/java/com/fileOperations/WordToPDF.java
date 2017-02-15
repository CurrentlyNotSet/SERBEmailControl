/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fileOperations;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.util.ExceptionHandler;
import com.util.JacobCOMBridge;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Andrew
 */
public class WordToPDF {

    /**
     * Creates PDF from DOCX, does this by opening file and "SaveAs" within
     * Microsoft Office itself and closing out.
     *
     * @param filePath String
     * @param fileName String
     * @return String - new File Name
     */
    public static String createPDF(String filePath, String fileName) {
        ActiveXComponent eolWord = null;
        String docxFile = filePath + fileName;
        String pdfFile = filePath + FilenameUtils.removeExtension(fileName) + ".pdf";

        File attachmentLocation = new File(filePath);
        if (!attachmentLocation.exists()) {
            attachmentLocation.mkdirs();
        }

        eolWord = JacobCOMBridge.setWordActive(true, false, eolWord);
        if (eolWord != null) {
            try {
                //Open MS Word & Save AS
                Dispatch document = eolWord.getProperty("Documents").toDispatch();
                Dispatch.call(document, "Open", docxFile).toDispatch();
                Dispatch WordBasic = Dispatch.call(eolWord, "WordBasic").getDispatch();
                Dispatch.call(WordBasic, "FileSaveAs", pdfFile, new Variant(17));
                Dispatch.call(document, "Close", new Variant(false));
                Thread.sleep(250);

                //Close out MS Word
                JacobCOMBridge.setWordActive(false, false, eolWord);
                Dispatch.call(eolWord, "Quit");
                eolWord.safeRelease();
                File oldDoc = new File(docxFile);
                oldDoc.delete();
                return FilenameUtils.getName(pdfFile);
            } catch (InterruptedException ex) {
                ExceptionHandler.Handle(ex);
                return "";
            }
        }
        return "";
    }

}
