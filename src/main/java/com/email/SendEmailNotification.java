/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.DocketNotificationModel;
import com.model.SystemEmailModel;
import com.sql.Audit;
import com.sql.DocketNotification;
import com.util.ExceptionHandler;
import com.util.Global;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author Andrew
 */
public class SendEmailNotification {

    /**
     * This sends a basic notification email that is just pure TEXT. Message is
     * sent from the section gathered
     *
     * @param eml DocketNotificationModel
     */
    public static void sendNotificationEmail(DocketNotificationModel eml) {
        //Get Account
        SystemEmailModel account = null;
        for (SystemEmailModel acc : Global.getSystemEmailParams()) {
            if (acc.getSection().equals(eml.getSection())) {
                account = acc;
                break;
            }
        }
        if (account != null) {
            String FROMaddress = account.getEmailAddress();
            String[] TOAddressess = ((eml.getSendTo() == null) ? "".split(";") : eml.getSendTo().split(";"));
            String subject = eml.getMessageSubject();
            String body = eml.getMessageBody();

            Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
            Properties properties = EmailProperties.setEmailOutProperties(account);

            Session session = Session.getInstance(properties, auth);
            MimeMessage email = new MimeMessage(session);

            try {
                for (String To : TOAddressess) {
                    if (EmailValidator.getInstance().isValid(To)) {
                        email.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
                    }
                }

                email.setFrom(new InternetAddress(FROMaddress));
                email.setSubject(subject);
                email.setText(body);
                if (Global.isOkToSendEmail()) {
                    Transport.send(email);
                } else {
                    Audit.addAuditEntry("Notification Not Actually Sent: " + eml.getId() + " - " + subject);
                }

                DocketNotification.deleteEmailEntry(eml.getId());
            } catch (AddressException ex) {
                ExceptionHandler.Handle(ex);
            } catch (MessagingException ex) {
                ExceptionHandler.Handle(ex);
            }
        }
    }

}
