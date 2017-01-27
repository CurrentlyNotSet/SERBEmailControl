/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.SystemEmailModel;
import com.model.SystemErrorModel;
import com.sql.SECExceptions;
import com.sql.SystemError;
import com.sql.SystemErrorEmailList;
import com.util.ExceptionHandler;
import com.util.Global;
import java.util.Calendar;
import java.util.List;
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
 * @author User
 */
public class SendEmailCrashReport {

    /**
     * Sends crash email to predetermined list from the database.
     * 
     * Also BCCs members of XLN team for notification of errors
     */
    public static void sendCrashEmail() {
        //Get Account
        SystemEmailModel account = null;
        for (SystemEmailModel acc : Global.getSystemEmailParams()) {
            if (acc.getSection().equals("ERROR")) {
                account = acc;
                break;
            }
        }

        if (account != null) {
            String FROMaddress = account.getEmailAddress();
            List<String> TOAddresses = SystemErrorEmailList.getActiveEmailAddresses();
            String[] BCCAddressess = ("Andrew.Schmidt@XLNSystems.com; Anthony.Perk@XLNSystems.com".split(";"));
            String subject = "SERB 3.0 Application Daily Error Report for " + Global.getMmddyyyy().format(Calendar.getInstance().getTime());
            String body = buildBody();

            Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
            Properties properties = EmailProperties.setEmailOutProperties(account);

            Session session = Session.getInstance(properties, auth);
            MimeMessage email = new MimeMessage(session);

            try {
                for (String TO : TOAddresses) {
                    if (EmailValidator.getInstance().isValid(TO)) {
                        email.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
                    }
                }

                for (String BCC : BCCAddressess) {
                    if (EmailValidator.getInstance().isValid(BCC)) {
                        email.addRecipient(Message.RecipientType.BCC, new InternetAddress(BCC));
                    }
                }

                email.setFrom(new InternetAddress(FROMaddress));
                email.setSubject(subject);
                email.setText(body);
                Transport.send(email);
            } catch (AddressException ex) {
                ExceptionHandler.Handle(ex);
            } catch (MessagingException ex) {
                ExceptionHandler.Handle(ex);
            }
        } else {
            System.out.println("No account found to send Error Email");
        }
    }

    /**
     * Builds the email body for the crash email
     * 
     * @return String (The built out body)
     */
    private static String buildBody() {
        String body = "";

        List<SystemErrorModel> errorList = SystemError.getErrorCounts();
        List<SystemErrorModel> emailErrorList = SECExceptions.getErrorCounts();

        if (errorList.size() > 0) {
            body += "These errors have been logged by the system today. \n\n";

            for (SystemErrorModel item : errorList) {
                body += item.getExceptionType() + ": " + String.valueOf(item.getNumber()) + "\n";
            }
        } else {
            body += "No errors have been thrown in the application today.";
        }

        body += "\n\n";

        if (emailErrorList.size() > 0) {
            body += "These errors have been logged by the email server today. \n\n";

            for (SystemErrorModel item : emailErrorList) {
                body += item.getExceptionType() + ": " + String.valueOf(item.getNumber()) + "\n";
            }
        } else {
            body += "No errors have been thrown in the Application today.";
        }

        body += "\n\n\n    - This is a system generated message.";
        return body;
    }

}
