/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

/**
 *
 * @author Andrew
 */
public class Main {

    /**
     * @param args the command line arguments 
     */
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("net.htmlparser.jericho").setLevel(java.util.logging.Level.OFF);
        System.out.println("\n\n\n");
        System.out.println("Starting SERB Email Server - v3.1.0");
        System.out.println("\n\n\n");
        MainClass bc = new MainClass();
        bc.setDefaults();
    }
}
