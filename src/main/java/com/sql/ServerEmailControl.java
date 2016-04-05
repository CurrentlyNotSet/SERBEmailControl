/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.util.ExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class ServerEmailControl {
    
    
    public static void updateCompletionTime(String column){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "UPDATE ServerEmailControl SET " + column + " = GETUTCDATE() WHERE id = 1";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }        
    }
}
