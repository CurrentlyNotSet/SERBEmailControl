/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.email.ReceiveEmail;
import com.email.SendEmail;
import com.email.SendEmailCalInvite;
import com.email.SendEmailNotification;
import com.model.DocketNotificationModel;
import com.model.EmailOutInvitesModel;
import com.model.EmailOutModel;
import com.model.SystemEmailModel;
import com.scans.ScansStamper;
import com.sql.DocketNotification;
import com.sql.EmailOut;
import com.sql.EmailOutInvites;
import com.sql.ServerEmailControl;
import com.sql.SystemEmail;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import com.util.StringUtilities;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Andrew
 */
public class MainClass {

    public void setDefaults() {
        if (FileService.setFolderPaths() && SystemEmail.loadEmailConnectionInformation()) {
            incomingEmails();
            //threads();
        } else {
            System.err.println("unable to resolve network connections");
        }
    }

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

    private void stampScansThread() {
        try {
            Thread.sleep(1000);
            while (true) {
                try {
                    stampScans();
                    //Printout the sleep information
                    System.out.println("Sleeping for: " + TimeUnit.MILLISECONDS.toMinutes(Global.getSleep()) + "min");

                    //Sleep the thread based on the INI file variable.
                    Thread.sleep(Global.getSleep());

                } catch (InterruptedException ex) {
                    ExceptionHandler.Handle(ex);
                    System.err.println("Thread Interrupted");
                }
            }
        } catch (InterruptedException ex) {
            ExceptionHandler.Handle(ex);
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
                    notificationEmails();
                    outgoingEmail();
                    SystemEmail.loadEmailConnectionInformation();
                    //Printout the sleep information
                    System.out.println("Sleeping for: " + TimeUnit.MILLISECONDS.toMinutes(Global.getSleep()) + "min");

                    //Sleep the thread based on the INI file variable.
                    Thread.sleep(Global.getSleep());

                } catch (InterruptedException ex) {
                    ExceptionHandler.Handle(ex);
                    System.err.println("Thread Interrupted");
                }
            }
        } catch (InterruptedException ex) {
            ExceptionHandler.Handle(ex);
            System.err.println("Thread Interrupted");
        }
    }

    private void stampScans() {
        System.out.println(StringUtilities.currentTime() + " - Started  Stamping Scans");
        ScansStamper.stampScans();
        System.out.println(StringUtilities.currentTime() + " - Finished Stamping Scans");
        ServerEmailControl.updateCompletionTime("stampScans");
    }

    private void incomingEmails() {
        System.out.println(StringUtilities.currentTime() + " - Started  Receiving Emails");
        for (SystemEmailModel account : Global.getSystemEmailParams()) {
            ReceiveEmail.fetchEmail(account);
        }
        System.out.println(StringUtilities.currentTime() + " - Finished Receiving Emails");
        ServerEmailControl.updateCompletionTime("incomingEmail");
    }

    private void calInvites() {
        System.out.println(StringUtilities.currentTime() + " - Started  Sending Calendar Invites");
        for (EmailOutInvitesModel email : EmailOutInvites.getQueuedEmailInvites()) {
            SendEmailCalInvite.sendCalendarInvite(email);
        }
        System.out.println(StringUtilities.currentTime() + " - Finished Sending Calendar Invites");
        ServerEmailControl.updateCompletionTime("calInvites");
    }

    private void notificationEmails() {
        System.out.println(StringUtilities.currentTime() + " - Started  Sending Notification Emails");
        for (DocketNotificationModel email : DocketNotification.getQueuedNotifications()) {
            SendEmailNotification.sendNotificationEmail(email);
        }
        System.out.println(StringUtilities.currentTime() + " - Finished Sending Notification Emails");
        ServerEmailControl.updateCompletionTime("notificationEmail");
    }

    private void outgoingEmail() {
        System.out.println(StringUtilities.currentTime() + " - Started  Sending Emails");
        for (EmailOutModel email : EmailOut.getEmailOutQueue()) {
            SendEmail.sendEmails(email);
        }
        System.out.println(StringUtilities.currentTime() + " - Finished Sending Emails");
        ServerEmailControl.updateCompletionTime("outgoingEmail");
    }

}
