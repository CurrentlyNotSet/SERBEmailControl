/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Andrew
 */
public class FileService {
    
    public static boolean setFolderPaths() {
        try {
            switch(InetAddress.getLocalHost().getHostName()) {
                case "Parkers-MacBook-Air.local":
                case "Parkers-Air":
                    Global.setScanPath("/Users/parkerjohnston/Desktop/SERB/Scan/");
                    Global.setEmailPath("/Users/parkerjohnston/Desktop/SERB/Email/");
                    Global.setActivityPath("/Users/parkerjohnston/Desktop/SERB/Activity/");
                    Global.setTestEmail(true);
                    return true;
                //TODO: Add in other machines with the correct paths
                case "Alienware15":
                case "Sniper":
                    Global.setScanPath("C:\\SERB\\Scan\\");
                    Global.setEmailPath("C:\\SERB\\Email\\");
                    Global.setActivityPath("C:\\SERB\\Activity\\");
                    Global.setTestEmail(true);
                    return true;
                case "CS12-SRB-ES1":
                    Global.setScanPath("C:\\SERB\\Scan\\");
                    Global.setEmailPath("C:\\SERB\\Email\\");
                    Global.setActivityPath("C:\\SERB\\Activity\\");
                    Global.setTestEmail(false);
                    return true;
                default:
                    Global.setScanPath("G:\\SERB\\Scan\\");
                    Global.setEmailPath("G:\\SERB\\Email\\");
                    Global.setActivityPath("G:\\SERB\\Activity\\");
                    Global.setTestEmail(false);
                    return true;
            }
            
        } catch (UnknownHostException ex) {
            SlackNotification.sendNotification(ex.getMessage());
            return false;
        }
    }

    public static boolean testFileLock(String path) {
        File PDFfile = new File(path);

        if (PDFfile.exists() && PDFfile.isDirectory() == false) {
            try {
                FileUtils.touch(PDFfile);
                return true;
            } catch (IOException e) {
                System.out.println("file in use: " + PDFfile);
                return false;
            }
        } else if (PDFfile.exists() == false) {
            System.out.println("file does not exist: " + PDFfile);
        } else if (PDFfile.isDirectory() == false) {
            System.out.println("file is a directory: " + PDFfile);
        }
        return false;
    }

    public static String getCaseFolderLocation(ActivityModel item) {
        return Global.getActivityPath()
                + File.separatorChar + item.getCaseType()
                + File.separatorChar + item.getCaseYear()
                + File.separatorChar + NumberFormatService.FullCaseNumber(item)
                + File.separatorChar + item.getFilePath();
    }

}