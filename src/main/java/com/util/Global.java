/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.SystemEmailModel;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Andrew
 */
public class Global {
        
    private static final int sleep = 0;
    public static Thread emailThread, scansThread;
    
    //Date Time Formatters
    private static final SimpleDateFormat mmddyyyyhhmmssa = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    
    //Folder Paths
    private static String scanPath;
    private static String emailPath;
    private static String activityPath;
    
    //System Email
    private static List<SystemEmailModel> incomingEmailParams;
    private static List<SystemEmailModel> outgoingEmailParams;

    public static int getSleep() {
        return sleep;
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

    public static List<SystemEmailModel> getIncomingEmailParams() {
        return incomingEmailParams;
    }

    public static void setIncomingEmailParams(List<SystemEmailModel> incomingEmailParams) {
        Global.incomingEmailParams = incomingEmailParams;
    }

    public static List<SystemEmailModel> getOutgoingEmailParams() {
        return outgoingEmailParams;
    }

    public static void setOutgoingEmailParams(List<SystemEmailModel> outgoingEmailParams) {
        Global.outgoingEmailParams = outgoingEmailParams;
    }

    public static SimpleDateFormat getMmddyyyyhhmmssa() {
        return mmddyyyyhhmmssa;
    }
}
