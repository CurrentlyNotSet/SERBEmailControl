/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.CMDSWebUpdater;

import com.Main;
import com.model.CasePartyModel;
import com.model.WEBCaseModel;
import com.model.WebBoardOrderModel;
import com.model.WebHistoryModel;
import com.sql.CaseParty;
import com.sql.WebBoardOrders;
import com.sql.WebCase;
import com.sql.WebHistory;
import com.util.Global;
import com.util.StringUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class CMDSwebUpdater {

    public static void processWebCaseList() {
        System.out.println("WebCase Start Time: " + new Date());

        String output = "";

        List<WEBCaseModel> list = WebCase.getWebCaseList();

        System.out.println("WebCase Gathered Cases Time: " + new Date());
        System.out.println("WebCase Gathered Cases : " + list.size());

        for (WEBCaseModel item : list) {
            String line = "";

            String AppellantName = "";
            String AppellantRepName = "";
            String AppellantRepTitle = "";
            String AppellantRepAdr1 = "";
            String AppellantRepAdr2 = "";
            String AppellantRepCSZ = "";
            String AppellantRepPhone = "";
            String AppelleeName = "";
            String AppelleeTitle = "";
            String AppelleeAdr1 = "";
            String AppelleeAdr2 = "";
            String AppelleeCSZ = "";
            String AppelleePhone = "";
            String AppelleeRepName = "";
            String AppelleeRepTitle = "";
            String AppelleeRepAdr1 = "";
            String AppelleeRepAdr2 = "";
            String AppelleeRepCSZ = "";
            String AppelleeRepPhone = "";

            List<CasePartyModel> partyList = CaseParty.getCasePartyList(
                    item.getYear(), item.getType(), item.getMonth(), item.getCaseSeqNumber());

            for (CasePartyModel person : partyList) {
                if (null != person.getCaseRelation()) {
                    switch (person.getCaseRelation()) {
                        case "Appellant":
                            AppellantName = StringUtilities.buildCasePartyName(person);
                            break;
                        case "Appellant Rep 1":
                            AppellantRepName = StringUtilities.buildCasePartyName(person);
                            AppellantRepTitle = person.getJobTitle();
                            AppellantRepAdr1 = person.getAddress1();
                            AppellantRepAdr2 = person.getAddress2();
                            AppellantRepCSZ = person.getCity() + ", " + person.getStateCode() + " " + person.getZipcode();
                            AppellantRepPhone = person.getPhone1();
                            break;
                        case "Appellee":
                            AppelleeName = StringUtilities.buildCasePartyName(person);
                            AppelleeTitle = person.getJobTitle();
                            AppelleeAdr1 = person.getAddress1();
                            AppelleeAdr2 = person.getAddress2();
                            AppelleeCSZ = person.getCity() + ", " + person.getStateCode() + " " + person.getZipcode();
                            AppelleePhone = person.getPhone1();
                            break;
                        case "Appellee Rep 1":
                            AppelleeRepName = StringUtilities.buildCasePartyName(person);
                            AppelleeRepTitle = person.getJobTitle();
                            AppelleeRepAdr1 = person.getAddress1();
                            AppelleeRepAdr2 = person.getAddress2();
                            AppelleeRepCSZ = person.getCity() + ", " + person.getStateCode() + " " + person.getZipcode();
                            AppelleeRepPhone = person.getPhone1();
                            break;
                        default:
                            break;
                    }
                }
            }

            //Case Information
            line += String.format("%-2s", item.getYear().substring(item.getYear().length() - 2));
            line += String.format("%-4s", item.getCaseSeqNumber());
            line += String.format("%-2s", item.getYear().substring(item.getYear().length() - 2));
            line += String.format("%-3s", item.getType());
            line += String.format("%-2s", item.getMonth());
            line += String.format("%-4s", item.getCaseSeqNumber());
            line += String.format("%-8s", item.getGroupNumber().equals("") ? "      " : item.getGroupNumber());
            line += String.format("%-3s", (item.getALJ() + "   ").substring(0, 3));

            //Party Information
            //(appellant)
            line += String.format("%-34s", AppellantName).substring(0, 34);

            //TR (appellant REP 1)
            line += String.format("%-34s", AppellantRepName).substring(0, 34);
            line += String.format("%-34s", AppellantRepTitle).substring(0, 34);
            line += String.format("%-34s", AppellantRepAdr1).substring(0, 34);
            line += String.format("%-34s", AppellantRepAdr2).substring(0, 34);
            line += String.format("%-34s", AppellantRepCSZ).substring(0, 34);
            line += String.format("%-10s", AppellantRepPhone).substring(0, 10);

            //Ae (Appellee)
            line += String.format("%-35s", AppelleeName).substring(0, 35);
            line += String.format("%-35s", AppelleeTitle).substring(0, 35);
            line += String.format("%-35s", AppelleeAdr1).substring(0, 35);
            line += String.format("%-35s", AppelleeAdr2).substring(0, 35);
            line += String.format("%-35s", AppelleeCSZ).substring(0, 35);
            line += String.format("%-10s", AppelleePhone).substring(0, 10);

            //Ar (Appellee Rep)
            line += String.format("%-35s", AppelleeRepName).substring(0, 35);
            line += String.format("%-35s", AppelleeRepTitle).substring(0, 35);
            line += String.format("%-35s", AppelleeRepAdr1).substring(0, 35);
            line += String.format("%-35s", AppelleeRepAdr2).substring(0, 35);
            line += String.format("%-35s", AppelleeRepCSZ).substring(0, 35);
            line += String.format("%-10s", AppelleeRepPhone).substring(0, 10);

            output += (output.trim().equals("") ? line : System.lineSeparator() + line);
        }

        System.out.println("WebCase Writing File Time: " + new Date());

        //Write File
        try {
            Files.write(Paths.get(Global.getDestinationPath() + Global.getWebCaseFileName()), output.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("WebCase End Time: " + new Date());
    }

    public static void processWebHistoryList() {
        String output = "";

        System.out.println("WebHistory Start Time: " + new Date());

        //Gather History List
        List<WebHistoryModel> list = WebHistory.getWebHistoryList();

        System.out.println("WebHistory Gathered Cases Time: " + new Date());
        System.out.println("WebHistory Gathered Cases : " + list.size());

        //Process Rows
        for (WebHistoryModel item : list) {
            String line = "";

            line += String.format("%-2s", item.getCaseYear());
            line += String.format("%-4s", item.getCaseNumber());
            line += "000000";
            line += String.format("%-2s", item.getCaseYear());
            line += String.format("%-3s", item.getCaseType());
            line += String.format("%-2s", item.getCaseMonth());
            line += String.format("%-4s", item.getCaseNumber());
            line += String.format("%-39s", item.getAppellant());
            line += String.format("%-10s", item.getEntryDate());
            line += " ";
            line += String.format("%-54s", item.getEntryDescription().replace("IN - ", "").replace("OUT - ", ""));

            output += (output.trim().equals("") ? line : System.lineSeparator() + line);
        }

        System.out.println("WebHistory Writing File Time: " + new Date());

        //Write File
        try {
            Files.write(Paths.get(Global.getDestinationPath() + Global.getWebHistoryFileName()), output.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("WebHistory End Time: " + new Date());
    }
    
    public static void processBoardOrdersCSV() {
        String output = "";

        System.out.println("WebBoardOrders Start Time: " + new Date());

        //Gather History List
        List<WebBoardOrderModel> list = WebBoardOrders.getWebBoardOrdersList();

        System.out.println("WebBoardOrders Gathered Cases Time: " + new Date());
        System.out.println("WebBoardOrders Gathered Cases : " + list.size());
        
        //Header List
        String showHideLine = "";
            
        showHideLine += "S,"; //CMDSCaseNumber
        showHideLine += "S,"; //View
        showHideLine += "H,"; //URL
        showHideLine += "S,"; //AppellantName
        showHideLine += "S,"; //AppelleeName
        showHideLine += "H,"; //OpenDate
        showHideLine += "S,"; //BoardOrderDate
        showHideLine += "S,"; //Result
        output += (output.trim().equals("") ? showHideLine : System.lineSeparator() + showHideLine);
        
        //Naming line
        String headerLine = "";
            
        headerLine += "Case Number,"; //CMDSCaseNumber
        headerLine += "View,"; //View
        headerLine += "URL,"; //URL
        headerLine += "Appellant,"; //AppellantName
        headerLine += "Appellee,"; //AppelleeName
        headerLine += "Open Date,"; //OpenDate
        headerLine += "Board Order Date,"; //BoardOrderDate
        headerLine += "Result,"; //Result
        output += (output.trim().equals("") ? headerLine : System.lineSeparator() + headerLine);
        
        //Process Rows
        for (WebBoardOrderModel item : list) {
            String line = "";
            
            line += item.getCMDSCaseNumber() + ",";
            line += item.getView() + ",";
            line += item.getURL() + ",";
            line += item.getAppellantName() + ",";
            line += item.getAppelleeName() + ",";
            line += item.getOpenDate() + ",";
            line += item.getBoardOrderDate() + ",";
            line += item.getResult() + ",";
            output += (output.trim().equals("") ? line : System.lineSeparator() + line);
        }

        System.out.println("WebBoardOrders Writing File Time: " + new Date());

        //Write File
        try {
            Files.write(Paths.get(Global.getDestinationPathBoardOrders() + Global.getWebBoardOrdersFileName()), output.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("WebBoardOrders End Time: " + new Date());
    }

}
