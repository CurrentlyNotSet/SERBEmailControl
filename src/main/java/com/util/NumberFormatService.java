/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import com.model.CaseTypeModel;
import com.model.EmailOutModel;

/**
 *
 * @author Andrew
 */
public class NumberFormatService {

    public static String FullCaseNumber(ActivityModel caseNumber) {
        return caseNumber.getCaseYear() + "-" 
                + caseNumber.getCaseType() + "-" 
                + caseNumber.getCaseMonth() + "-" 
                + caseNumber.getCaseNumber();
    }

    public static String FullCaseNumber(EmailOutModel caseNumber) {
        return caseNumber.getCaseYear() + "-" 
                + caseNumber.getCaseType() + "-" 
                + caseNumber.getCaseMonth() + "-" 
                + caseNumber.getCaseNumber();
    }

    public static String getSection(String caseType) {
        for (CaseTypeModel item : Global.getCaseTypeList()) {
            if (item.getCaseType().equalsIgnoreCase(caseType)) {
                return item.getSection();
            }
        }
        return caseType;
    }
    
}
