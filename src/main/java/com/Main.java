/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.util.Global;

/**
 *
 * @author Andrew
 */
public class Main {

    /**
     * @param args the command line arguments Versioning follows YEAR*.MONTH.DAY
     * YEAR = (# of release years; 1 = first year released '2017')
     */
    public static void main(String[] args) {
        //Setup Application
        setEnv(args);
        displayWelcome();

        //Run Application
//        MainClass bc = new MainClass();
//        bc.setDefaults();
    }

    private static void setEnv(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("net.htmlparser.jericho").setLevel(java.util.logging.Level.OFF);

        //this logic will only only grab the first matching arg and use that, subsquential args will not work
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-incoming") || arg.equalsIgnoreCase("-outgoing") || arg.equalsIgnoreCase("-all")) {
                switch (arg) {
                    case "-incoming":
                        Global.setOutgoingOk(false);
                        break;
                    case "-outgoing":
                        Global.setIncomingOk(false);
                        break;
                    case "-all":
                        Global.setIncomingOk(true);
                        Global.setOutgoingOk(true);
                        break;
                    default:
                }
                break;
            }
        }
    }

    private static void displayWelcome() {
        System.out.println("\n\n\n");
        System.out.println("Starting SERB Email Server - v" + Global.getVersion());
        if (!Global.isOkToSendEmail()){
            System.out.println("    Email Server in TEST mode, no email will be sent.");
        }
        System.out.println("");
        System.out.println("Incoming Email Process: " + Global.isIncomingOk());
        System.out.println("Outgoing Email Process: " + Global.isOutgoingOk());
        System.out.println("\n\n\n");
    }
}
