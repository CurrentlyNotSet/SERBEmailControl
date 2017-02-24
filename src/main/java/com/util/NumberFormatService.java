/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.ActivityModel;
import com.model.CaseTypeModel;
import com.model.EmailOutModel;
import com.model.RelatedCaseModel;

/**
 *
 * @author Andrew
 */
public class NumberFormatService {

    /**
     * Gets four columns worth of case number information and converts it to
     * its single case number
     * 
     * @param caseNumber ActivityModel
     * @return String full case number
     */
    public static String FullCaseNumber(ActivityModel caseNumber) {
        return caseNumber.getCaseYear() + "-" 
                + caseNumber.getCaseType() + "-" 
                + caseNumber.getCaseMonth() + "-" 
                + caseNumber.getCaseNumber();
    }

    /**
     * Gets four columns worth of case number information and converts it to
     * its single case number
     * 
     * @param related RelatedCaseModel
     * @return String full case number
     */
    public static String FullCaseNumber(RelatedCaseModel related) {
        return related.getRelatedCaseYear() + "-" 
                + related.getRelatedCaseType() + "-" 
                + related.getRelatedCaseMonth() + "-" 
                + related.getRelatedCaseNumber();
    }
    
    /**
     * Gets four columns worth of case number information and converts it to
     * its single case number
     * 
     * @param caseNumber EmailOutModel
     * @return String full case number
     */
    public static String FullCaseNumber(EmailOutModel caseNumber) {
        return caseNumber.getCaseYear() + "-" 
                + caseNumber.getCaseType() + "-" 
                + caseNumber.getCaseMonth() + "-" 
                + caseNumber.getCaseNumber();
    }

    /**
     * Gets section based off of casetype from case number
     * @param caseType String
     * @return String (section)
     */
    public static String getSection(String caseType) {
        for (CaseTypeModel item : Global.getCaseTypeList()) {
            if (item.getCaseType().equalsIgnoreCase(caseType)) {
                return item.getSection();
            }
        }
        return caseType;
    }
    
}
