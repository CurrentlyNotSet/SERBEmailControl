/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import java.util.Date;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author parker.johnston
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
        
    public static String properAttachmentName(String filename, int emailID, int attachmentNumber) {
        String base = FilenameUtils.removeExtension(filename).replace("/", "-").replace(":", "").replace("\"", "");
        String extension = FilenameUtils.getExtension(filename);
        String number = String.valueOf(attachmentNumber);
        if (attachmentNumber < 10){
            number = "0" + number;
        }
        return emailID + "_" + number + "_" + base + "." + extension;
    }
    
    public static String currentTime(){
        return Global.getMmddyyyyhhmmssa().format(new Date());
    }
}
