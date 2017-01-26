/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailMessageModel;
import com.util.ExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class EMail {
    
    /**
     * Inserts email message into email table. 
     * 
     * @param eml EmailMessageModel
     * @return Integer - generated key of the email
     */
    public static int InsertEmail(EmailMessageModel eml){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO EMail ("
                    + "section, "
                    + "emailFrom, "
                    + "emailTo, "
                    + "emailSubject, "
                    + "sentDate, "
                    + "receivedDate, "
                    + "emailCC, "
                    + "emailBCC, "
                    + "emailBody, "
                    + "emailBodyFileName, "
                    + "readyToFile "
                    + ") VALUES ("
                    + "?, " //1
                    + "?, " //2
                    + "?, " //3
                    + "?, " //4
                    + "?, " //5
                    + "?, " //6
                    + "?, " //7
                    + "?, " //8
                    + "?, " //9
                    + "?, " //10
                    + "0)"; // Ready to File False
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString   ( 1, eml.getSection());
            ps.setString   ( 2, eml.getEmailFrom());
            ps.setString   ( 3, eml.getEmailTo());
            ps.setString   ( 4, eml.getEmailSubject());
            ps.setTimestamp( 5, eml.getSentDate());
            ps.setTimestamp( 6, eml.getReceivedDate());
            ps.setString   ( 7, eml.getEmailCC());
            ps.setString   ( 8, eml.getEmailBCC());
            ps.setString   ( 9, eml.getEmailBody());
            ps.setString   (10, eml.getEmailBodyFileName());
            ps.executeUpdate();
            ResultSet newRow = ps.getGeneratedKeys();
            if (newRow.next()){
                return newRow.getInt(1);
            }
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
        return 0;
    }    
    
    /**
     * Marks an email ready to file by the system. This is in place so a user 
     * does not try to docket an email that is currently being processed.
     * 
     * @param eml
     */
    public static void setEmailReadyToFile(EmailMessageModel eml){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "UPDATE EMail SET readyToFile = ?, emailBodyFileName = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt   (1, eml.getReadyToFile());
            ps.setString(2, eml.getEmailBodyFileName());
            ps.setInt   (3, eml.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }        
    }
    
}
