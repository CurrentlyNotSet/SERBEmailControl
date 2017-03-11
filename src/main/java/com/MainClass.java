/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.email.ReceiveEmail;
import com.email.SendEmail;
import com.email.SendEmailCalInvite;
import com.email.SendEmailCrashReport;
import com.email.SendEmailNotification;
import com.model.DocketNotificationModel;
import com.model.EmailOutInvitesModel;
import com.model.EmailOutModel;
import com.model.SystemEmailModel;
import com.scans.ScansStamper;
import com.sql.Audit;
import com.sql.CaseType;
import com.sql.DBBackupScript;
import com.sql.DocketNotification;
import com.sql.EmailOut;
import com.sql.EmailOutInvites;
import com.sql.SECExceptions;
import com.sql.ServerEmailControl;
import com.sql.SystemEmail;
import com.util.ExceptionHandler;
import com.util.FileService;
import com.util.Global;
import com.util.StringUtilities;
import com.util.TimerSettings;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Andrew
 */
public class MainClass {

    /**
     * setDefaults() verifies and checks to make sure the folder paths and the
     * connection information is available to the applications before running.
     *
     * Then it starts the different threads that the application runs
     */
    public void setDefaults() {
        if (FileService.setFolderPaths() && SystemEmail.loadEmailConnectionInformation()) {
            Global.setCaseTypeList(CaseType.getCaseTypes());
            threads();
        } else {
            System.err.println("unable to resolve network connections");
        }
    }

    /**
     * threads() runs several different threads simultaneously Email Thread
     * Scans Thread Daily Crash Email (Scheduled Task) Refresh Email Accounts
     * (Scheduled Task) REMOVED //Database Cleanup (Scheduled Task) DISABLED
     * //Database Backups (Scheduled Task) DISABLED
     */
    private void threads() {
        Thread emailThread, scansThread;

        long oneDay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

        // every night at 2am you run your task
        Timer timer = new Timer();

        emailThread = new Thread() {
            @Override
            public void run() {
                emailThreads();
            }
        };

//        scansThread = new Thread() {
//            @Override
//            public void run() {
//                stampScansThread();
//            }
//        };
        //Run Tasks
        timer.scheduleAtFixedRate(new dailyCrashNotifyEmail(), TimerSettings.errorEmailTime(), oneDay);
//        timer.scheduleAtFixedRate(new databaseCleanupTask(), TimerSettings.dbCleanupTime(), oneDay);
//        timer.scheduleAtFixedRate(new databaseBackups(), TimerSettings.dbBackupTime(), oneDay);
        emailThread.start();
//        scansThread.start();
    }

    /**
     * Timer task for sending daily crash email.
     */
    private static class dailyCrashNotifyEmail extends TimerTask {

        @Override
        public void run() {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                SendEmailCrashReport.sendCrashEmail();
            }
        }
    }

    /**
     * Timer task for the clean up of the database.
     */
    private static class databaseCleanupTask extends TimerTask {

        @Override
        public void run() {
            Audit.removeOldAudits();
            SECExceptions.removeOldExceptions();
        }
    }

    /**
     * Timer task for the back up of the database.
     */
    private static class databaseBackups extends TimerTask {

        @Override
        public void run() {
            for (String databaseName : Global.getBackupDatabases()) {
                DBBackupScript.backupDB(databaseName);
            }
        }
    }

    /**
     * Thread for stamping scans
     */
    private void stampScansThread() {
        try {
            Thread.sleep(1000);
            while (true) {
                try {
                    stampScans();
                    //Printout the sleep information
                    System.out.println("Stamp Thread - Sleeping for: "
                            + TimeUnit.MILLISECONDS.toMinutes(Global.getSleep()) + "min \n");

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

    /**
     * Thread for sending email.
     */
    private void emailThreads() {
        try {
            Thread.sleep(1000);
            while (true) {
                try {
                    System.out.println(StringUtilities.currentTime()
                            + " - Starting Email Thread");
                    SystemEmail.loadEmailConnectionInformation();
                    incomingEmails();
                    calInvites();
                    notificationEmails();
                    outgoingEmail();
                    //Printout the sleep information
                    System.out.println("Email Thread - Sleeping for: "
                            + TimeUnit.MILLISECONDS.toSeconds(Global.getSleep()) + "sec \n");

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

    /**
     * Stamp scans
     */
    private void stampScans() {
        long lStartTime = System.currentTimeMillis();
        ScansStamper.stampScans();
        long lEndTime = System.currentTimeMillis();
        System.out.println(StringUtilities.currentTime()
                + " - Finished Stamping Scans (" + StringUtilities.convertLongToTime(lEndTime - lStartTime) + ")");
        ServerEmailControl.updateCompletionTime("stampScans");
    }

    /**
     * Gather incoming emails by looping through accounts
     */
    private void incomingEmails() {
        long lStartTime = System.currentTimeMillis();
        for (SystemEmailModel account : Global.getSystemEmailParams()) {
            ReceiveEmail.fetchEmail(account);
        }
        long lEndTime = System.currentTimeMillis();
        System.out.println(StringUtilities.currentTime()
                + " - Finished Receiving Emails (" + StringUtilities.convertLongToTime(lEndTime - lStartTime) + ")");
        ServerEmailControl.updateCompletionTime("incomingEmail");
    }

    /**
     * Send calendar invites by looping through accounts
     */
    private void calInvites() {
        long lStartTime = System.currentTimeMillis();
        for (EmailOutInvitesModel email : EmailOutInvites.getQueuedEmailInvites()) {
            SendEmailCalInvite.sendCalendarInvite(email);
        }
        long lEndTime = System.currentTimeMillis();
        System.out.println(StringUtilities.currentTime()
                + " - Finished Sending Calendar Invites (" + StringUtilities.convertLongToTime(lEndTime - lStartTime) + ")");
        ServerEmailControl.updateCompletionTime("calInvites");
    }

    /**
     * Send notifications by looping through accounts
     */
    private void notificationEmails() {
        long lStartTime = System.currentTimeMillis();
        for (DocketNotificationModel email : DocketNotification.getQueuedNotifications()) {
            SendEmailNotification.sendNotificationEmail(email);
        }
        long lEndTime = System.currentTimeMillis();
        System.out.println(StringUtilities.currentTime()
                + " - Finished Sending Notification Emails (" + StringUtilities.convertLongToTime(lEndTime - lStartTime) + ")");
        ServerEmailControl.updateCompletionTime("notificationEmail");
    }

    /**
     * Send outgoing emails by looping through accounts
     */
    private void outgoingEmail() {
        long lStartTime = System.currentTimeMillis();
        for (EmailOutModel email : EmailOut.getEmailOutQueue()) {
            SendEmail.sendEmails(email);
        }
        long lEndTime = System.currentTimeMillis();
        System.out.println(StringUtilities.currentTime()
                + " - Finished Sending Emails (" + StringUtilities.convertLongToTime(lEndTime - lStartTime) + ")");
        ServerEmailControl.updateCompletionTime("outgoingEmail");
    }

}
