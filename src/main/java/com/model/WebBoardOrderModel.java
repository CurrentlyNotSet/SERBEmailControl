/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

/**
 *
 * @author Andrew
 */
public class WebBoardOrderModel {
    
    private String CMDSCaseNumber;
    private String View;
    private String URL;
    private String AppellantName;
    private String AppelleeName;
    private String OpenDate;
    private String BoardOrderDate;
    private String Result;

    public String getCMDSCaseNumber() {
        return CMDSCaseNumber;
    }

    public void setCMDSCaseNumber(String CMDSCaseNumber) {
        this.CMDSCaseNumber = CMDSCaseNumber;
    }

    public String getView() {
        return View;
    }

    public void setView(String View) {
        this.View = View;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAppellantName() {
        return AppellantName;
    }

    public void setAppellantName(String AppellantName) {
        this.AppellantName = AppellantName;
    }

    public String getAppelleeName() {
        return AppelleeName;
    }

    public void setAppelleeName(String AppelleeName) {
        this.AppelleeName = AppelleeName;
    }

    public String getOpenDate() {
        return OpenDate;
    }

    public void setOpenDate(String OpenDate) {
        this.OpenDate = OpenDate;
    }

    public String getBoardOrderDate() {
        return BoardOrderDate;
    }

    public void setBoardOrderDate(String BoardOrderDate) {
        this.BoardOrderDate = BoardOrderDate;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }
    
    
    
}
