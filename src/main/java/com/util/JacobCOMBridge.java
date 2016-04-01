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

    public static ActiveXComponent setWordActive(boolean active, boolean visible, ActiveXComponent eolWord) {
        final String libFile = "amd64".equals(System.getProperty("os.arch")) ? "jacob-1.18-x64.dll" : "jacob-1.18-x86.dll";
        final String dllPath = new File(libFile).getAbsolutePath();        
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

    private static boolean loadLibrary(final String dllPath) {
        try {
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, dllPath);
            LibraryLoader.loadJacobLibrary();
            return true;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return false;
        }
    }

}
