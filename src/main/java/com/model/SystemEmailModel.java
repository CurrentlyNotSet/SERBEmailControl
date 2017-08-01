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
public class SystemEmailModel {

    private int id;
    private int active;
    private String section;
    private String emailAddress;
    private String username;
    private String password;
    private String incomingURL;
    private int incomingPort;
    private String incomingProtocol;
    private String incomingFolder;
    private String outgoingURL;
    private int outgoingPort;
    private String outgoingProtocol;
    private String outgoingFolder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIncomingURL() {
        return incomingURL;
    }

    public void setIncomingURL(String incomingURL) {
        this.incomingURL = incomingURL;
    }

    public int getIncomingPort() {
        return incomingPort;
    }

    public void setIncomingPort(int incomingPort) {
        this.incomingPort = incomingPort;
    }

    public String getIncomingProtocol() {
        return incomingProtocol;
    }

    public void setIncomingProtocol(String incomingProtocol) {
        this.incomingProtocol = incomingProtocol;
    }

    public String getIncomingFolder() {
        return incomingFolder;
    }

    public void setIncomingFolder(String incomingFolder) {
        this.incomingFolder = incomingFolder;
    }

    public String getOutgoingURL() {
        return outgoingURL;
    }

    public void setOutgoingURL(String outgoingURL) {
        this.outgoingURL = outgoingURL;
    }

    public int getOutgoingPort() {
        return outgoingPort;
    }

    public void setOutgoingPort(int outgoingPort) {
        this.outgoingPort = outgoingPort;
    }

    public String getOutgoingProtocol() {
        return outgoingProtocol;
    }

    public void setOutgoingProtocol(String outgoingProtocol) {
        this.outgoingProtocol = outgoingProtocol;
    }

    public String getOutgoingFolder() {
        return outgoingFolder;
    }

    public void setOutgoingFolder(String outgoingFolder) {
        this.outgoingFolder = outgoingFolder;
    }

}
