/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.SystemEmailModel;
import com.util.Global;
import java.util.Properties;

/**
 *
 * @author Andrew
 */
public class EmailProperties {

    /**
     * This method sets the email in properties for IMAP or POP accounts
     *
     * @param account SystemEmailModel
     * @return Properties for email account access
     */
    public static Properties setEmailInProperties(SystemEmailModel account) {
        Properties properties = new Properties();

        properties.setProperty("mail.store.protocol", account.getIncomingProtocol());
        if (null != account.getIncomingProtocol()) {
            switch (account.getIncomingProtocol()) {
                case "imap":
                case "imaps":
                    properties.setProperty("mail.imap.submitter", account.getUsername());
                    properties.setProperty("mail.imap.auth", "true");
                    properties.setProperty("mail.imap.host", account.getIncomingURL());
                    properties.put("mail.imap.port", String.valueOf(account.getIncomingPort()));
                    properties.setProperty("mail.imap.partialfetch", "false");
                    properties.setProperty("mail.imaps.partialfetch", "false");
                    break;
                case "pop":
                    properties.setProperty("mail.pop3s.host", account.getIncomingURL());
                    properties.put("mail.pop3s.port", String.valueOf(account.getIncomingPort()));
                    properties.put("mail.pop3s.starttls.enable", "true");
                    break;
                default:
                    break;
            }
        }
        if (Global.isDebug()) {
            properties.setProperty("mail.debug", "true");
        }
        return properties;
    }

    /**
     * This method sets the email properties for sending out email via SMTP
     * protocol.
     *
     * @param account
     * @return
     */
    public static Properties setEmailOutProperties(SystemEmailModel account) {
        Properties properties = new Properties();

        properties.setProperty("mail.store.protocol", account.getOutgoingProtocol());
        if (null != account.getOutgoingProtocol()) {
            switch (account.getOutgoingProtocol()) {
                case "smtp":
                    properties.setProperty("mail.smtp.submitter", account.getUsername());
                    properties.setProperty("mail.smtp.host", account.getOutgoingURL());
                    properties.put("mail.smtp.port", String.valueOf(account.getOutgoingPort()));
                    properties.setProperty("mail.smtp.quitwait", "true");
                    break;
                default:
                    break;
            }
        }
        if (Global.isDebug()) {
            properties.setProperty("mail.debug", "true");
        }
        return properties;
    }
}
