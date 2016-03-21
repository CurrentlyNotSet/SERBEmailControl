/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import java.sql.Timestamp;

/**
 *
 * @author Andrew
 */
public class EmailMessageModel {
    
    private int id;
    private String section;
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    private Timestamp sentDate;
    private Timestamp receivedDate;
    private String emailCC;
    private String emailBCC;
    private String emailBody;
    private String emailBodyFileName;
    private int readyToFile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public Timestamp getSentDate() {
        return sentDate;
    }

    public void setSentDate(Timestamp sentDate) {
        this.sentDate = sentDate;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getEmailCC() {
        return emailCC;
    }

    public void setEmailCC(String emailCC) {
        this.emailCC = emailCC;
    }

    public String getEmailBCC() {
        return emailBCC;
    }

    public void setEmailBCC(String emailBCC) {
        this.emailBCC = emailBCC;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailBodyFileName() {
        return emailBodyFileName;
    }

    public void setEmailBodyFileName(String emailBodyFileName) {
        this.emailBodyFileName = emailBodyFileName;
    }

    public int getReadyToFile() {
        return readyToFile;
    }

    public void setReadyToFile(int readyToFile) {
        this.readyToFile = readyToFile;
    }
    
}
