/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.model.CasePartyModel;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Andrew
 */
public class StringUtilities {

    // adapted from post by Phil Haack and modified to match better
    private final static String tagStart
            = "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
    private final static String tagEnd
            = "\\</\\w+\\>";
    private final static String tagSelfClosing
            = "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
    private final static String htmlEntity
            = "&[a-zA-Z][a-zA-Z0-9]+;";
    private final static Pattern htmlPattern = Pattern.compile(
            "(" + tagStart + ".*" + tagEnd + ")|(" + tagSelfClosing + ")|(" + htmlEntity + ")",
            Pattern.DOTALL);

    /**
     * Will return true if s contains HTML markup tags or entities.
     *
     * @param s String to test
     * @return true if string contains HTML
     */
    public static boolean isHtml(String s) {
        boolean ret=false;
        if (s != null) {
            ret=htmlPattern.matcher(s).find();
        }
        return ret;
    }

    /**
     * Strips MS Office tags from copy/paste into emails. Outlook will recognize
     * However nothing else does and Jericho throws a fit with parsing.
     * 
     * @param text
     * @return String text Cleaned
     */
    public static String replaceOfficeTags(String text){
        
        text = text.replaceAll("<![if !supportLists]>", "    ");
        text = text.replaceAll("<![endif]>", "");
        
        
        
        return text;
    }
    
    /**
     * Builds out the filename for the attachment. order is...
     * [emailID_number_base.extension]
     *
     * @param filename String
     * @param emailID Integer
     * @param attachmentNumber Integer
     * @return
     */
    public static String properAttachmentName(String filename, int emailID, int attachmentNumber) {
        String base = FilenameUtils.removeExtension(filename).replace("[^A-Za-z0-9]", "_");
        String extension = FilenameUtils.getExtension(filename);
        String number = String.valueOf(attachmentNumber);
        if (attachmentNumber < 10){
            number = "0" + number;
        }
        return emailID + "_" + number + "_" + base + "." + extension;
    }

    /**
     * Gets the current time in "MM/dd/yyyy HH:mm:ss a" Format
     *
     * @return String
     */
    public static String currentTime(){
        return Global.getMmddyyyyhhmmssa().format(new Date());
    }

    /**
     * converts millis to [__hr __min __sec] format
     *
     * @param millis long
     * @return String of duration
     */
    public static String convertLongToTime(long millis) {
        String duration = String.format("%02dhr %02dmin %02dsec",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        if (TimeUnit.MILLISECONDS.toHours(millis) == 0) {
            String[] split = duration.split("hr");
            duration = split[1].trim();
        }
        return duration.trim();
    }

    public static String getDepartmentByCaseType(String caseType) {
        String section = NumberFormatService.getSection(caseType);
        if (section.equalsIgnoreCase("CMDS") || section.equalsIgnoreCase("CSC")){
            return "SPBR";
        } else {
            return "SERB";
        }
    }

    /**
     * Building the Case Party Name for the CMDS Web Updater
     * @param item
     * @return
     */
    public static String buildCasePartyName(CasePartyModel item) {
        String fullName = "";
        fullName = fullName.trim() + (item.getLastName().equals("") ? "" : item.getLastName().trim());
        fullName = fullName.trim() + (item.getFirstName().equals("") ? "" : ", " + item.getFirstName().trim());
        fullName = fullName.trim() + (item.getMiddleInitial().equals("") ? "" : " " + (item.getMiddleInitial().trim().length() == 1 ? item.getMiddleInitial().trim() + "." : item.getMiddleInitial().trim()));

        if (!item.getCompanyName().equals("")) {
            if (fullName.trim().equals("")) {
                fullName = item.getCompanyName().trim();
            }
        }

        return fullName.trim();
    }

}
