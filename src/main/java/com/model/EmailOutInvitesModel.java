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
public class EmailOutInvitesModel {
    
    private int id;
    private String section;
    private String toAddress;
    private String ccAddress;
    private String emailBody;
    private String caseNumber;
    private String hearingType;
    private String hearingRoomAbv;
    private String hearingDescription;
    private Timestamp hearingStartTime;
    private Timestamp hearingEndTime;

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

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getHearingType() {
        return hearingType;
    }

    public void setHearingType(String hearingType) {
        this.hearingType = hearingType;
    }

    public String getHearingRoomAbv() {
        return hearingRoomAbv;
    }

    public void setHearingRoomAbv(String hearingRoomAbv) {
        this.hearingRoomAbv = hearingRoomAbv;
    }

    public String getHearingDescription() {
        return hearingDescription;
    }

    public void setHearingDescription(String hearingDescription) {
        this.hearingDescription = hearingDescription;
    }

    public Timestamp getHearingStartTime() {
        return hearingStartTime;
    }

    public void setHearingStartTime(Timestamp hearingStartTime) {
        this.hearingStartTime = hearingStartTime;
    }

    public Timestamp getHearingEndTime() {
        return hearingEndTime;
    }

    public void setHearingEndTime(Timestamp hearingEndTime) {
        this.hearingEndTime = hearingEndTime;
    }

}
