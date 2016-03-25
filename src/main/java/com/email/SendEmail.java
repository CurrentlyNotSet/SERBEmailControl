/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.SystemEmailModel;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Session;
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

}
