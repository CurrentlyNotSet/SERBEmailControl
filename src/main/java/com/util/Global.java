/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.SystemEmailModel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Andrew
 */
public class Global {
    
    //Operational Parameters
    private static final boolean debug = false;
    private static final int sleep = 300000; //milliseconds  (5 min)
    private static final String exceptionTimeFrame = "month";
    private static final String exceptionTimeAmount = "1";
    private static final String auditTimeFrame = "year";
    private static final String auditTimeAmount = "1";
    private static final int hourOfPurge = 1; //24hr clock
    private static final int hourOfDBBackup = 1; //24hr clock
    
    //Date Time Formatters
    private static final SimpleDateFormat mmddyyyyhhmmssa = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    private static final SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
    private static final SimpleDateFormat mmddyyyy = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat hhmmssa = new SimpleDateFormat("HH:mm:ss a");
    
    //Folder Paths
    private static String scanPath;
    private static String emailPath;
    private static String activityPath;
    
    //MSSQL Backup Information
    private static final String databaseBackupPath = "c:\\backupPath";
    private static final List<String> backupDatabases = Arrays.asList(
            "SERB", "SERB-DEMO");
    
    //System Email
    private static List<SystemEmailModel> systemEmailParams;

    //Attachment FileType Blacklist
    private static final List<String> fileBlackList = Arrays.asList(
            ".??_", ".?Q?", ".?Z?", ".7z", ".a", ".ace", ".afa", ".alz", 
            ".apk", ".ar", ".arc", ".arj", ".b1", ".ba", ".bat", ".bh", 
            ".bz2", ".cab", ".car", ".cfs", ".cpio", ".cpt",".dar", ".dd", 
            ".dgc", ".dmg", ".ear", ".exe", ".F", ".gca", ".gz", ".ha", 
            ".hki", ".ice", ".infl", ".iso", ".jar", ".kgb", ".LBR", ".lbr", 
            ".lha", ".lz", ".lzo", ".lzh", ".lzx", ".mar", ".msi", ".pak", 
            ".partimg", ".paq6", ".paq7", ".paq8", ".pea", ".pim", ".pit", 
            ".qda", ".rar", ".rk", ".rz", ".s7z", ".sda", ".sea", ".sen", 
            ".sfark", ".sfx", ".shar", ".sit", ".sitx", ".sqx", ".sz", ".tar", 
            ".tar.gz", ".tgz.tar.Z", ".tar.bz2",".tbz2", ".tar.lzma", ".tlz", 
            ".uc ", ".uc0", ".uc2", ".ucn", ".ur2", ".ue2", ".uca", ".uha", 
            ".vcf", ".war", ".wim", ".xar", ".xp3", ".xz", ".yz1", ".z", 
            ".zip", ".zipx", ".zoo", ".zpaq", ".zz");

    public static boolean isDebug() {
        return debug;
    }

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

    public static List<SystemEmailModel> getSystemEmailParams() {
        return systemEmailParams;
    }

    public static void setSystemEmailParams(List<SystemEmailModel> systemEmailParams) {
        Global.systemEmailParams = systemEmailParams;
    }
    
    public static SimpleDateFormat getMmddyyyyhhmmssa() {
        return mmddyyyyhhmmssa;
    }

    public static SimpleDateFormat getiCalendarDateFormat() {
        return iCalendarDateFormat;
    }

    public static List<String> getFileBlackList() {
        return fileBlackList;
    }

    public static SimpleDateFormat getMmddyyyy() {
        return mmddyyyy;
    }

    public static SimpleDateFormat getHhmmssa() {
        return hhmmssa;
    }

    public static String getExceptionTimeFrame() {
        return exceptionTimeFrame;
    }

    public static String getAuditTimeFrame() {
        return auditTimeFrame;
    }

    public static int getHourOfPurge() {
        return hourOfPurge;
    }

    public static String getExceptionTimeAmount() {
        return exceptionTimeAmount;
    }

    public static String getAuditTimeAmount() {
        return auditTimeAmount;
    }

    public static List<String> getBackupDatabases() {
        return backupDatabases;
    }

    public static String getDatabaseBackupPath() {
        return databaseBackupPath;
    }

    public static int getHourOfDBBackup() {
        return hourOfDBBackup;
    }

}
