/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import com.model.EmailOutModel;
import static com.sun.media.jai.codec.TIFFEncodeParam.COMPRESSION_GROUP4;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Andrew
 */
public class FileService {

    public static boolean setFolderPaths() {
        try {
            switch (InetAddress.getLocalHost().getHostName()) {
                case "Parkers-MacBook-Air.local":
                case "Parkers-Air":
                    Global.setScanPath("/Users/parkerjohnston/Desktop/SERB/Scan/");
                    Global.setEmailPath("/Users/parkerjohnston/Desktop/SERB/Email/");
                    Global.setActivityPath("/Users/parkerjohnston/Desktop/SERB/Activity/");
                    return true;
                case "Alienware15":
                case "Sniper":
                    Global.setScanPath("C:\\SERB\\Scan\\");
                    Global.setEmailPath("C:\\SERB\\Email\\");
                    Global.setActivityPath("C:\\SERB\\Activity\\");
                    return true;
//                case "CS12-SRB-ES1":  //OUR SERB Machine
//                    Global.setScanPath("C:\\SERB\\Scan\\");
//                    Global.setEmailPath("C:\\SERB\\Email\\");
//                    Global.setActivityPath("C:\\SERB\\Activity\\");
//                    return true;
                default:
                    Global.setScanPath("G:\\SERB\\Scan\\");
                    Global.setEmailPath("G:\\SERB\\Email\\");
                    Global.setActivityPath("G:\\SERB\\Activity\\");
                    return true;
            }

        } catch (UnknownHostException ex) {
            ExceptionHandler.Handle(ex);
            return false;
        }
    }

    public static boolean testFileLock(String path) {
        File PDFfile = new File(path);

        if (PDFfile.exists() && PDFfile.isDirectory() == false) {
            try {
                FileUtils.touch(PDFfile);
                return true;
            } catch (IOException ex) {
                System.out.println("file in use: " + PDFfile);
                ExceptionHandler.Handle(ex);
                return false;
            }
        } else if (PDFfile.exists() == false) {
            System.out.println("file does not exist: " + PDFfile);
        } else if (PDFfile.isDirectory() == false) {
            System.out.println("file is a directory: " + PDFfile);
        }
        return false;
    }

    public static String getCaseFolderFileLocation(ActivityModel item) {
        return Global.getActivityPath()
                + File.separatorChar + NumberFormatService.getSection(item.getCaseType())
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar + item.getFileName();
    }
    
    public static String getCaseFolderLocation(EmailOutModel item) {
        return Global.getActivityPath()
                + File.separatorChar + NumberFormatService.getSection(item.getCaseType())
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar;
    }

    public static String getCaseFolderORGCSCFileLocation(ActivityModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseNumber()
                + File.separatorChar + item.getFileName();
    }
    
    public static String getCaseFolderORGCSCLocation(EmailOutModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseNumber()
                + File.separatorChar;
    }
    
    public static boolean isImageFormat(String image) {
        return image.toLowerCase().endsWith(".jpg")
                || ((image.toLowerCase().endsWith(".tif")
                || image.toLowerCase().endsWith(".tiff"))
                && PDFBoxTools.TIFFCompression(image) == COMPRESSION_GROUP4)
                || image.toLowerCase().endsWith(".gif")
                || image.toLowerCase().endsWith(".bmp")
                || image.toLowerCase().endsWith(".png");
    }

    public static boolean isValidAttachment(String file) {
        if (file != null) {
            String ext = FilenameUtils.getExtension(file);
            return !Global.getFileBlackList().contains(ext);
        }
        return false;
    }
}
