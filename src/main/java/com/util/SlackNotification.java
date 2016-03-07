/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO: Update file to Slack (DB Backup)

/**
 *
 * @author parkerjohnston
 */
public class SlackNotification {
    
    public static void sendNotification(String message) {
        try {
            new Slack(SlackInfo.getSlackHook())
                    .icon(SlackInfo.getSlackIcon()) // Ref - http://www.emoji-cheat-sheet.com/
                    .sendToChannel(SlackInfo.getSlackChannel())
                    .displayName(SlackInfo.getSlackUser())
                    .push(new SlackMessage(message));
        } catch (IOException ex) {
            Logger.getLogger(SlackNotification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
//    public static void sendAttachement(String filePath) {
//        
//        try {
//            new Slack(SlackInfo.getSlackHook())
//                .icon(SlackInfo.getSlackIcon()) // Ref - http://www.emoji-cheat-sheet.com/
//                .sendToChannel(SlackInfo.getSlackChannel())
//                .displayName(SlackInfo.getSlackUser())
//                .push(new SlackAttachment(filePath));
//        } catch (IOException ex) {
//            Logger.getLogger(SlackNotification.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
}