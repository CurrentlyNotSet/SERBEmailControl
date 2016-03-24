/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.SystemEmailModel;
import javax.mail.PasswordAuthentication;

/**
 *
 * @author Andrew
 */
public class EmailAuthenticator {
    
    public static javax.mail.Authenticator setEmailAuthenticator(SystemEmailModel account) {
        javax.mail.Authenticator auth = new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        account.getUsername(), account.getPassword());
            }
        };
        return auth;
    }
    
}
