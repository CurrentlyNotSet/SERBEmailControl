/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.DocketNotificationModel;
import com.model.SystemEmailModel;
import com.sql.DocketNotification;
import com.util.Global;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Andrew
 */
public class SendEmailNotification {
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
            String[] TOAddressess = eml.getSendTo().split(";");
            String subject = eml.getMessageSubject();
            String body = eml.getMessageBody();

            Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
            Properties properties = EmailProperties.setEmailOutProperties(account);
            Session session = Session.getInstance(properties, auth);
            MimeMessage smessage = new MimeMessage(session);

            try {
                for (String To : TOAddressess) {
                    smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
                }
                
                smessage.addFrom(new InternetAddress[]{new InternetAddress(FROMaddress)});
                smessage.setSubject(subject);
                smessage.setText(body);
                Transport.send(smessage);
                
                DocketNotification.deleteEmailEntry(eml.getId());
            } catch (AddressException ex) {
                Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
