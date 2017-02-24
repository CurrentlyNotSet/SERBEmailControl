/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import com.model.EmailOutModel;
import com.model.RelatedCaseModel;
import com.model.SECExceptionsModel;
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

    /**
     * sets the folder paths for the file locations depending on the host 
     * computer the application is running from.
     * 
     * @return boolean for file paths not available
     */
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

    /**
     * This tests the file lock to see if a file is in use if there is an issue 
     * with the file beyond being locked then an exception is thrown and placed
     * into the database.
     * 
     * @param path String
     * @return boolean
     */
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
//            System.out.println("file does not exist: " + PDFfile);
            SECExceptionsModel item = new SECExceptionsModel();
            item.setClassName("FileService");
            item.setMethodName("testFileLock");
            item.setExceptionType("FileMissing");
            item.setExceptionDescription("Can't Stamp Scan, File Missing: " + PDFfile);
            ExceptionHandler.HandleNoException(item);
        } else if (PDFfile.isDirectory() == false) {
//            System.out.println("file is a directory: " + PDFfile);
            SECExceptionsModel item = new SECExceptionsModel();
            item.setClassName("FileService");
            item.setMethodName("testFileLock");
            item.setExceptionType("NotAFile");
            item.setExceptionDescription("Can't Stamp Scan, is Directory: " + PDFfile);
        }
        return false;
    }

    /**
     * Get case file location
     * 
     * @param item ActivityModel
     * @return String file path
     */
    public static String getCaseFolderFileLocation(ActivityModel item) {
        return Global.getActivityPath()
                + File.separatorChar + NumberFormatService.getSection(item.getCaseType())
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar + item.getFileName();
    }

    /**
     * Get case file location
     * 
     * @param item EmailOutModel
     * @return String file path
     */
    public static String getCaseFolderLocation(EmailOutModel item) {
        return Global.getActivityPath()
                + File.separatorChar + NumberFormatService.getSection(item.getCaseType())
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar;
    }

    /**
     * Get case file location
     * 
     * @param item RelatedCaseModel
     * @return String file path
     */
    public static String getCaseFolderLocationRelatedCase(RelatedCaseModel item) {
        return Global.getActivityPath()
                + File.separatorChar + NumberFormatService.getSection(item.getCaseType())
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar;
    }
    
    /**
     * Gets case folder location for ORG or CSC cases
     * 
     * @param item ActivityModel
     * @return String file path
     */
    public static String getCaseFolderORGCSCFileLocation(ActivityModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseNumber()
                + File.separatorChar + item.getFileName();
    }

    /**
     * Gets case folder location for ORG or CSC cases
     * 
     * @param item EmailOutModel
     * @return String file path
     */
    public static String getCaseFolderORGCSCLocation(EmailOutModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseNumber()
                + File.separatorChar;
    }

    /**
     * Gets case folder location for ORG or CSC cases
     * 
     * @param item RelatedCaseModel
     * @return String file path
     */
    public static String getCaseFolderORGCSCLocation(RelatedCaseModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseNumber()
                + File.separatorChar;
    }
    
    /**
     * This method checks the file extension to see if it is a valid image
     * format.
     *
     * @param image String
     * @return boolean is image format
     */
    public static boolean isImageFormat(String image) {
        return image.toLowerCase().endsWith(".jpg")
                || ((image.toLowerCase().endsWith(".tif")
                || image.toLowerCase().endsWith(".tiff"))
                && PDFBoxTools.TIFFCompression(image) == COMPRESSION_GROUP4)
                || image.toLowerCase().endsWith(".gif")
                || image.toLowerCase().endsWith(".bmp")
                || image.toLowerCase().endsWith(".png");
    }

    /**
     * Checks to see if the file is a valid attachment type for receiving
     * 
     * @param file String
     * @return boolean
     */
    public static boolean isValidAttachment(String file) {
        if (file != null) {
            String ext = FilenameUtils.getExtension(file);
            return !Global.getFileBlackList().contains(ext);
        }
        return false;
    }
    
}
