/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.calInvites.*;
import com.email.recieveEmail;
import com.model.SystemEmailModel;
import com.outgoingEmail.*;
import com.scans.ScansStamper;
import com.sql.SystemEmail;
import com.util.FileService;
import com.util.Global;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Andrew
 */
public class MainClass {
    
    public void setDefaults() {
        if (FileService.setFolderPaths() && SystemEmail.loadEmailConnectionInformation()){
            threads();
        } else {
            System.err.println("unable to resolve network connections");
        }
    }
    
    /**
     * The thread for in indexing. Separating it out allows for the updating of 
     * the AWT elements so we can duplicate the console text.
     */
    private void threads() {
        Global.emailThread = new Thread() {
            @Override
            public void run() {
                emailThreads();
            }
        };
        
        Global.scansThread = new Thread() {
            @Override
            public void run() {
                stampScansThread();
            }
        };
        
        Global.emailThread.start();
        Global.scansThread.start();
    }

    private void stampScansThread(){
        try {
            Thread.sleep(1000);
            while (true) {
                try {
                    ScansStamper.stampScans();
                    //Printout the sleep information
                    System.out.println("Sleeping for: " + TimeUnit.MILLISECONDS.toMinutes(Global.getSleep()) + "min");

                    //Sleep the thread based on the INI file variable.
                    Thread.sleep(Global.getSleep());
                    
                } catch (InterruptedException ex) {
                    System.err.println("Thread Interrupted");
                }
            }
        } catch (InterruptedException ex) {
            System.err.println("Thread Interrupted");
        }
    }
        
    public void emailThreads() {
        try {
            Thread.sleep(1000);
            while (true) {
                try {
                    incomingEmails();
                    calInvites();
                    outgoingEmail();
                    //Printout the sleep information
                    System.out.println("Sleeping for: " + TimeUnit.MILLISECONDS.toMinutes(Global.getSleep()) + "min");

                    //Sleep the thread based on the INI file variable.
                    Thread.sleep(Global.getSleep());
                    
                } catch (InterruptedException ex) {
                    System.err.println("Thread Interrupted");
                }
            }
        } catch (InterruptedException ex) {
            System.err.println("Thread Interrupted");
        }
    }
    
    private void incomingEmails() {
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Started  Receiving Emails");
        
        for (SystemEmailModel account : Global.getSystemEmailParams()){
            recieveEmail.fetchEmail(account);
        }
        
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Finished Receiving Emails");
    }
    
    private void calInvites() {
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Started  Sending Calendar Invites");
        
        //SERB Emails
        MEDinvites.sendCalInvite();
        REPinvites.sendCalInvite();
        ORGinvites.sendCalInvite();
        ULPinvites.sendCalInvite();
        HearingsInvites.sendCalInvite();
        
        //PBR Emails
        CMDSinvites.sendCalInvite();
        CSCinvites.sendCalInvite();
        
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Finished Sending Calendar Invites");
    }
    
    private void outgoingEmail() {
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Started  Sending Emails");
        
        //SERB Emails
        MEDoutgoingEmails.sendEmails();
        REPoutgoingEmails.sendEmails();
        ORGoutgoingEmails.sendEmails();
        ULPoutgoingEmails.sendEmails();
        HearingsOutgoingEmails.sendEmails();
        
        //PBR Emails
        CMDSoutgoingEmails.sendEmails();
        CSCoutgoingEmails.sendEmails();
        
        System.out.println(Global.getMmddyyyyhhmmssa().format(new Date()) + " - Finished Sending Emails");
    }
    
}


