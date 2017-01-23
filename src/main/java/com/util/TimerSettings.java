/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Andrew
 */
public class TimerSettings {
    
    public static Date dbCleanupTime(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, Global.getHourOfPurge());
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        return date.getTime();
    }
    
    public static Date dbBackupTime(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, Global.getHourOfDBBackup());
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        return date.getTime();
    }
    
    public static Date errorEmailTime(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, Global.getHourOfErrorEmail());
        date.set(Calendar.MINUTE, 2);
        date.set(Calendar.SECOND, 0);
        return date.getTime();
    }
}
