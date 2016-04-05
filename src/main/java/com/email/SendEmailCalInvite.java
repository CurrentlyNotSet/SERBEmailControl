/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.EmailOutInvitesModel;
import com.model.SystemEmailModel;
import com.sql.EmailOutInvites;
import com.util.Global;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Andrew
 */
public class SendEmailCalInvite {
    
    public static void sendCalendarInvite(EmailOutInvitesModel eml) {
        SystemEmailModel account = null;

        //Get Account
        for (SystemEmailModel acc : Global.getSystemEmailParams()) {
            if (acc.getSection().equals(eml.getSection())) {
                account = acc;
                break;
            }
        }
        if (account != null) {
            //Get parts
            String FromAddress = account.getEmailAddress();
            String[] TOAddressess = eml.getToAddress().split(";");
            String[] CCAddressess = eml.getCcAddress().split(";");
            String emailSubject = Subject(eml);
            BodyPart emailBody = body(eml);
            BodyPart inviteBody = invite(eml, account, emailSubject);

            //Set Email Parts
            Authenticator auth = EmailAuthenticator.setEmailAuthenticator(account);
            Properties properties = EmailProperties.setEmailOutProperties(account);
            Session session = Session.getInstance(properties, auth);
            MimeMessage smessage = new MimeMessage(session);
            Multipart multipart = new MimeMultipart("alternative");
            try {
                smessage.addFrom(new InternetAddress[]{new InternetAddress(FromAddress)});
                for (String To : TOAddressess) {
                    smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
                }
                for (String Cc : CCAddressess) {
                    smessage.addRecipient(Message.RecipientType.CC, new InternetAddress(Cc));
                }
                smessage.setSubject(emailSubject);
                multipart.addBodyPart(emailBody);
                multipart.addBodyPart(inviteBody);
                smessage.setContent(multipart);
                Transport.send(smessage);
                EmailOutInvites.deleteEmailEntry(eml.getId());
            } catch (AddressException ex) {
                Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static String Subject(EmailOutInvitesModel eml) {
        return "Upcoming " + eml.getHearingType() 
                + " hearing for Case Number: " + eml.getCaseNumber();
    }
    
    private static BodyPart body(EmailOutInvitesModel eml) {
        MimeBodyPart descriptionPart = new MimeBodyPart();
        try {
            String content = "There has been a " + eml.getHearingDescription()
                    + " scheduled for " + eml.getCaseNumber()
                    + " on " + Global.getMmddyyyy().format(eml.getHearingStartTime())
                    + " at " + Global.getHhmmssa().format(eml.getHearingStartTime()) 
                    + "\n\n\n";
            descriptionPart.setContent(content, "text/html; charset=utf-8");
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmailCalInvite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return descriptionPart;
    }
    
    private static BodyPart invite(EmailOutInvitesModel eml, SystemEmailModel account, String emailSubject) {
        BodyPart calendarPart = new MimeBodyPart();
        try {            
            String calendarContent =
                    "BEGIN:VCALENDAR\n"
                    + "METHOD:REQUEST\n"
                    + "PRODID: BCP - Meeting\n"
                    + "VERSION:2.0\n"
                    + "BEGIN:VEVENT\n"
                    + "DTSTAMP:" + Global.getiCalendarDateFormat().format(eml.getHearingStartTime()) + "\n"
                    + "DTSTART:" + Global.getiCalendarDateFormat().format(eml.getHearingStartTime()) + "\n"
                    + "DTEND:" + Global.getiCalendarDateFormat().format(eml.getHearingEndTime()) + "\n"
                    + "SUMMARY: " + emailSubject.replace("Upcoming", "").trim() + "\n" + //Subject
                    "UID:324\n"
                    + "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:" + new InternetAddress(account.getEmailAddress()).getAddress() + "\n" + //return email
                    "ORGANIZER:MAILTO:" + new InternetAddress(account.getEmailAddress()).getAddress() + "\n" + //return email
                    "LOCATION: " + eml.getHearingRoomAbv() + "\n" + //hearing room
                    "DESCRIPTION: " + emailSubject + "\n" + //subject
                    "SEQUENCE:0\n"
                    + "PRIORITY:5\n"
                    + "CLASS:PUBLIC\n"
                    + "STATUS:CONFIRMED\n"
                    + "TRANSP:OPAQUE\n"
                    + "BEGIN:VALARM\n"
                    + "ACTION:DISPLAY\n"
                    + "DESCRIPTION:REMINDER\n"
                    + "TRIGGER;RELATED=START:-PT00H15M00S\n"
                    + "END:VALARM\n"
                    + "END:VEVENT\n"
                    + "END:VCALENDAR";
            calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
            calendarPart.setContent(calendarContent, "text/calendar;method=CANCEL");
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmailCalInvite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return calendarPart;
    }
    
}
