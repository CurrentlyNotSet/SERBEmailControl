/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.SECExceptionsModel;
import com.sql.SECExceptions;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class ExceptionHandler {
    
    
    public static void Handle(Exception ex) {
        SECExceptionsModel item = new SECExceptionsModel();
        item.setClassName(Thread.currentThread().getStackTrace()[2].getClassName());
        item.setMethodName(Thread.currentThread().getStackTrace()[2].getMethodName());
        item.setExceptionType(ex.getClass().getSimpleName());
        item.setExceptionDescription(ex.toString());
        
        //Print out to commandline
        Logger.getLogger(ex.getMessage());
        
        //Send to the Server
        SECExceptions.insertException(item);
    }
    
    
        
}
