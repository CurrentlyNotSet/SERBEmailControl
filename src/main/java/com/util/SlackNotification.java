/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import java.io.IOException;

/**
 *
 * @author Andrew
 */
public class SlackNotification {

    /**
     * Sends notification to slack 
     * 
     * @param message String
     */
    public static void sendNotification(String message) {
        try {
            new Slack(SlackInfo.getSlackHook())
                    .icon(SlackInfo.getSlackIcon()) // Ref - http://www.emoji-cheat-sheet.com/
                    .sendToChannel(SlackInfo.getSlackChannel())
                    .displayName(SlackInfo.getSlackUser())
                    .push(new SlackMessage(message));
        } catch (IOException ex) {
            ExceptionHandler.Handle(ex);
        }
    }

}
