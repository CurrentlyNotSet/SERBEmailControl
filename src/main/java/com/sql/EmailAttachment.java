/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.util.SlackNotification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class EmailAttachment {
    
    public void insertEmailAttachment(int EmailID, String attachmentPath){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO EmailAttachment ("
                    + "id, "
                    + "emailID, "
                    + "fileName "
                    + ") VALUES ("
                    + "default, "
                    + "?, "
                    + "?)";
            ps = conn.prepareStatement(sql);
            ps.setInt   (1, EmailID);
            ps.setString(2, attachmentPath);
            ps.executeUpdate();
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
}