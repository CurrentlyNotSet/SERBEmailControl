/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.email;

import com.model.EmailOutInvitesModel;
import com.model.SystemEmailModel;
import com.sql.Audit;
import com.sql.EmailOutInvites;
import com.util.ExceptionHandler;
import com.util.Global;
import java.util.Properties;
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
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author Andrew
 */
public class SendEmailCalInvite {

    /**
     * Sends email based off of the section it comes from. This creates a
     * calendar invite object that is interactive by Outlook.
     *
     * @param eml EmailOutInviteModel
     */
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
            String[] TOAddressess = ((eml.getToAddress() == null) ? "".split(";") : eml.getToAddress().split(";"));
            String[] CCAddressess = ((eml.getCcAddress() == null) ? "".split(";") : eml.getCcAddress().split(";"));
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
                    if (EmailValidator.getInstance().isValid(To)) {
                        smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(To));
                    }
                }
                for (String Cc : CCAddressess) {
                    if (EmailValidator.getInstance().isValid(Cc)) {
                        smessage.addRecipient(Message.RecipientType.CC, new InternetAddress(Cc));
                    }
                }
                smessage.setSubject(emailSubject);
                multipart.addBodyPart(emailBody);
                multipart.addBodyPart(inviteBody);
                smessage.setContent(multipart);
                if (Global.isOkToSendEmail()) {
                    Transport.send(smessage);
                } else {
                    Audit.addAuditEntry("Cal Invite Not Actually Sent: " + eml.getId() + " - " + emailSubject);
                }
                EmailOutInvites.deleteEmailEntry(eml.getId());
            } catch (AddressException ex) {
                ExceptionHandler.Handle(ex);
            } catch (MessagingException ex) {
                ExceptionHandler.Handle(ex);
            }
        }
    }

    /**
     * Builds the subject for the email
     *
     * @param eml EmailOutInvitesModel
     * @return String (Subject)
     */
    private static String Subject(EmailOutInvitesModel eml) {
        return "Upcoming " + eml.getHearingType()
                + " hearing for Case Number: " + eml.getCaseNumber();
    }

    /**
     * Builds the body for the email
     *
     * @param eml EmailOutInvitesModel
     * @return String (Body)
     */
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
            ExceptionHandler.Handle(ex);
        }
        return descriptionPart;
    }

    /**
     * Builds the calendar invite for the email
     *
     * @param eml EmailOutInvitesModel
     * @return BodyPart (calendar invite)
     */
    private static BodyPart invite(EmailOutInvitesModel eml, SystemEmailModel account, String emailSubject) {
        BodyPart calendarPart = new MimeBodyPart();
        try {
            String calendarContent
                    = "BEGIN:VCALENDAR\n"
                    + "METHOD:REQUEST\n"
                    + "PRODID: BCP - Meeting\n"
                    + "VERSION:2.0\n"
                    + "BEGIN:VEVENT\n"
                    + "DTSTAMP:" + Global.getiCalendarDateFormat().format(eml.getHearingStartTime()) + "\n"
                    + "DTSTART:" + Global.getiCalendarDateFormat().format(eml.getHearingStartTime()) + "\n"
                    + "DTEND:" + Global.getiCalendarDateFormat().format(eml.getHearingEndTime()) + "\n"
                    //Subject
                    + "SUMMARY: " + emailSubject.replace("Upcoming", "").trim() + "\n"
                    + "UID:324\n"
                    //return email
                    + "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:" + new InternetAddress(account.getEmailAddress()).getAddress() + "\n"
                    //return email
                    + "ORGANIZER:MAILTO:" + new InternetAddress(account.getEmailAddress()).getAddress() + "\n"
                    //hearing room
                    + "LOCATION: " + eml.getHearingRoomAbv() + "\n"
                    //subject
                    + "DESCRIPTION: " + emailSubject + "\n"
                    + "SEQUENCE:0\n"
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
            ExceptionHandler.Handle(ex);
        }
        return calendarPart;
    }

}
