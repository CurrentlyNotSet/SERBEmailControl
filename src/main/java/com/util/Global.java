/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import java.text.SimpleDateFormat;

/**
 *
 * @author Andrew
 */
public class Global {
        
    private static final int sleep = 0;
    private static boolean dev = false;
    public static Thread emailThread, scansThread;
    
    //Date Time Formatters
    private static final SimpleDateFormat mmddyyyyhhmmssa = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    
    //Folder Paths
    private static String scanPath;
    private static String emailPath;
    private static String activityPath;
    
    
    public static SimpleDateFormat getMmddyyyyhhmmssa() {
        return mmddyyyyhhmmssa;
    }

    public static int getSleep() {
        return sleep;
    }
    
    public static boolean isDev() {
        return dev;
    }

    public static void setDev(boolean dev) {
        Global.dev = dev;
    }

    public static String getScanPath() {
        return scanPath;
    }

    public static void setScanPath(String scanPath) {
        Global.scanPath = scanPath;
    }

    public static String getEmailPath() {
        return emailPath;
    }

    public static void setEmailPath(String emailPath) {
        Global.emailPath = emailPath;
    }

    public static String getActivityPath() {
        return activityPath;
    }

    public static void setActivityPath(String activityPath) {
        Global.activityPath = activityPath;
    }

}
