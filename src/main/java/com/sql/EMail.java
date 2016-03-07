/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailModel;
import com.util.SlackNotification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class EMail {
    
    public static int InsertEmail(EmailModel eml){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO EMail ("
                    + "id, "
                    + "section, "
                    + "emailFrom, "
                    + "emailTo, "
                    + "emailSubject, "
                    + "sentDate, "
                    + "receivedDate, "
                    + "emailCC, "
                    + "emailBCC, "
                    + "emailBody, "
                    + "emailBodyFileName "
                    + ") VALUES ("
                    + "default, "
                    + "?, " //1
                    + "?, " //2
                    + "?, " //3
                    + "?, " //4
                    + "?, " //5
                    + "?, " //6
                    + "?, " //7
                    + "?, " //8
                    + "?, " //9
                    + "?)"; //10
            ps = conn.prepareStatement(sql);
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
            ResultSet newRow = ps.getResultSet();
            return newRow.getInt("id");
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
        return 0;
    }
    
}
