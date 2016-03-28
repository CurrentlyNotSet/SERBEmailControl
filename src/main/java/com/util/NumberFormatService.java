/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import com.model.EmailOutModel;

/**
 *
 * @author Andrew
 */
public class NumberFormatService {
    
    public static String FullCaseNumber(ActivityModel caseNumber) {
        return caseNumber.getCaseYear() + "-" + caseNumber.getCaseType() + "-" + caseNumber.getCaseMonth() + "-" + caseNumber.getCaseNumber();
    }
    
    public static String FullCaseNumber(EmailOutModel caseNumber) {
        return caseNumber.getCaseYear() + "-" + caseNumber.getCaseType() + "-" + caseNumber.getCaseMonth() + "-" + caseNumber.getCaseNumber();
    }
    
    public static ActivityModel parseFullCaseNumber(String fullCaseNumber) {
        ActivityModel caseNumber = new ActivityModel();
        String[] parsedCaseNumber = fullCaseNumber.split("-");
        caseNumber.setCaseYear(parsedCaseNumber[0]);
        caseNumber.setCaseType(parsedCaseNumber[1]);
        caseNumber.setCaseMonth(parsedCaseNumber[2]);
        caseNumber.setCaseNumber(parsedCaseNumber[3]);
        return caseNumber;   
    }
}
