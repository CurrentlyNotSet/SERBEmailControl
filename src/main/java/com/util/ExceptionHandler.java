/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.SECExceptionsModel;
import com.sql.SECExceptions;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class ExceptionHandler {
    
    
    public static void Handle(Exception ex) {
        SECExceptionsModel item = new SECExceptionsModel();
        item.setExceptionDescription(ex.toString());
        item.setClassName(Thread.currentThread().getStackTrace()[2].getClassName());
        item.setMethodName(Thread.currentThread().getStackTrace()[2].getMethodName());
        item.setExceptionType(ex.getClass().getSimpleName());
        
        //Print out to commandline
        Logger.getLogger(ExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);
        
        //Send to the Server
        SECExceptions.insertException(item);
    }
    
    
        
}
