/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import java.io.File;

/**
 *
 * @author Andrew
 */
public class JacobCOMBridge {

    /**
     * COM bridge to talk to Microsoft Office for conversion of DOCX to PDF
     * 
     * @param active boolean
     * @param visible boolean
     * @param eolWord ActiveXComponent
     * @return ActiveXComponent
     */
    public static ActiveXComponent setWordActive(boolean active, boolean visible, ActiveXComponent eolWord) {
        final String libFile = "amd64".equals(System.getProperty("os.arch")) ? "jacob-1.18-x64.dll" : "jacob-1.18-x86.dll";
        String dllPath = "";
        File dll = new File(libFile);
        if (dll.exists()) {
            dllPath = dll.getAbsolutePath();
        } else {
            dllPath = System.getenv("WINDIR") + "\\system32\\" + libFile;
        }
        
        if (loadLibrary(dllPath)) {
            if (active) {
                if (eolWord == null) {
                    eolWord = new ActiveXComponent("Word.Application");
                }
                eolWord.setProperty("Visible", new Variant(visible));
            } else {
                if (eolWord != null) {
                    eolWord.invoke("Quit", new Variant[0]);
                }
                eolWord = null;
            }
        }
        return eolWord;
    }

    /**
     * Load the library file, jacob.dll file. 
     * 
     * @param dllPath String
     * @return boolean
     */
    private static boolean loadLibrary(final String dllPath) {
        try {
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, dllPath);
            LibraryLoader.loadJacobLibrary();
            return true;
        } catch (UnsatisfiedLinkError e) {
            SlackNotification.sendNotification(e.toString());
            return false;
        }
    }

}
