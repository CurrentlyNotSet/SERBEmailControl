/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author User
 */
public class CalendarCalculation {
        
    public static Timestamp adjustTimeZoneOffset(Timestamp localTime){
        TimeZone tz = TimeZone.getDefault();  
        Calendar cal = GregorianCalendar.getInstance(tz);
        long offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        
        long adjustedTime = localTime.getTime() - offsetInMillis;
        
        return new Timestamp(adjustedTime);
    }
    
}
