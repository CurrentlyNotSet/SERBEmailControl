/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.SystemEmailModel;
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
public class SendEmail {

    public static void sendEmails(SystemEmailModel account) {
        Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
        Properties properties = EmailProperties.setEmailOutProperties(account);

        //NEED FOR LOOP WITH MESSAGES
        Session session = Session.getInstance(properties, auth);
        MimeMessage smessage = new MimeMessage(session);
    }

    public static void sendNotificationEmail(SystemEmailModel account){
        String FROMaddress = "";
        String TOaddress = "";
        String subject = "";
        String body = "";
        
        Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
        Properties properties = EmailProperties.setEmailOutProperties(account);
        Session session = Session.getInstance(properties, auth);
        MimeMessage smessage = new MimeMessage(session);

        try {
            smessage.addFrom(new InternetAddress[]{new InternetAddress(FROMaddress)});
            smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(TOaddress));
            smessage.setSubject(subject);
            smessage.setText(body);
            Transport.send(smessage);
        } catch (AddressException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
