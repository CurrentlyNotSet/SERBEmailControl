/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

/**
 *
 * @author User
 */
public class WebHistoryModel {

    private String CaseYear;
    private String CaseType;
    private String CaseMonth;
    private String CaseNumber;
    private String Appellant;
    private String EntryDate;
    private String EntryDescription;

    public String getCaseYear() {
        return CaseYear;
    }

    public void setCaseYear(String CaseYear) {
        this.CaseYear = CaseYear;
    }

    public String getCaseType() {
        return CaseType;
    }

    public void setCaseType(String CaseType) {
        this.CaseType = CaseType;
    }

    public String getCaseMonth() {
        return CaseMonth;
    }

    public void setCaseMonth(String CaseMonth) {
        this.CaseMonth = CaseMonth;
    }

    public String getCaseNumber() {
        return CaseNumber;
    }

    public void setCaseNumber(String CaseNumber) {
        this.CaseNumber = CaseNumber;
    }

    public String getAppellant() {
        return Appellant;
    }

    public void setAppellant(String Appellant) {
        this.Appellant = Appellant;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String EntryDate) {
        this.EntryDate = EntryDate;
    }

    public String getEntryDescription() {
        return EntryDescription;
    }

    public void setEntryDescription(String EntryDescription) {
        this.EntryDescription = EntryDescription;
    }

}
