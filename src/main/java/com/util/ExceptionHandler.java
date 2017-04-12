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

    /**
     * Exception handler for an item that has a catch. This method places an
     * SECExceptionsModel item into the database
     *
     * @param ex Exception
     */
    public static void Handle(Exception ex) {
        SECExceptionsModel item = new SECExceptionsModel();
        item.setClassName(Thread.currentThread().getStackTrace()[2].getClassName());
        item.setMethodName(Thread.currentThread().getStackTrace()[2].getMethodName());
        item.setExceptionType(ex.getClass().getSimpleName());
        item.setExceptionDescription(ex.toString());

        if (ex.getClass().getSimpleName().equals("MessagingException")){
            if (ex.toString().toLowerCase().contains("connection dropped by server")){
                item.setExceptionType("Connection Dropped by Server");
            }else if (ex.toString().toLowerCase().contains("connection reset")){
                item.setExceptionType("Connection Reset");
            }else if (ex.toString().toLowerCase().contains("bad user authenticated")){
                item.setExceptionType("User Unable To Connect");
            }
        }

        //Print out to commandline
        Logger.getLogger(ex.getMessage());

        //Send to the Server
        if (SECExceptions.insertException(item)) {
            //true = failed out || send to Slack instead
            SlackNotification.sendNotification(ex.toString());
        }
    }

    /**
     * Exception handler for an item that has no catch. This method places the
     * SECExceptionsModel item into the database
     *
     * @param item SECExceptionsModel
     */
    public static void HandleNoException(SECExceptionsModel item) {
        boolean insert = true;
        if (item.getExceptionDescription().startsWith("Can't Send Email, File Missing for EmailID:")
                && SECExceptions.getExistingException(item.getExceptionDescription()) > 0) {
            insert = false;
        } else if (item.getExceptionDescription().startsWith("Can't Send Email, File In Use for EmailID:")
                && SECExceptions.getExistingException(item.getExceptionDescription()) > 0) {
            insert = false;
        } else if (item.getExceptionDescription().startsWith("Can't Stamp Scan, ")
                && SECExceptions.getExistingException(item.getExceptionDescription()) > 0) {
            insert = false;
        }

        if (insert) {
            //Print out to commandline
            Logger.getLogger(item.getExceptionDescription());

            //Send to the Server
            if (SECExceptions.insertException(item)) {
                //true = failed out, send to Slack instead
                SlackNotification.sendNotification(item.getExceptionDescription());
            }
        }
    }

}
