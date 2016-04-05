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
       
    public static String createPDF(String filePath, String fileName) {
        ActiveXComponent eolWord = null;
        String docxFile = filePath + fileName;
        String pdfFile = filePath + FilenameUtils.removeExtension(fileName) + ".pdf";
        try {
            eolWord = JacobCOMBridge.setWordActive(true, false, eolWord);
            Dispatch Documents = eolWord.getProperty("Documents").toDispatch();
            Dispatch.call(Documents, "Open", docxFile).toDispatch();
            Dispatch WordBasic = Dispatch.call(eolWord, "WordBasic").getDispatch();
            Dispatch.call(WordBasic, "FileSaveAs", pdfFile, new Variant(17));
            Dispatch.call(Documents, "Close", new Variant(false));
            Thread.sleep(250);
            JacobCOMBridge.setWordActive(false, false, eolWord);
            File oldDoc = new File(docxFile);
            oldDoc.delete();
        } catch (InterruptedException ex) {
            ExceptionHandler.Handle(ex);
            return "";
        }
        return FilenameUtils.getName(pdfFile);
    }
    
}
