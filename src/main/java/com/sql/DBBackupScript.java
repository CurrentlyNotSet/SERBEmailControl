/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.util.ExceptionHandler;
import com.util.Global;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class DBBackupScript {
    
    public static void backupDB(String databaseName){
            Connection conn = null;
            PreparedStatement ps = null;
        try {    
            conn = DBConnection.connectToDBforBackup();
            String sql = "BACKUP DATABASE " + databaseName 
                    + " TO DISK = '" + Global.getDatabaseBackupPath() + "' ";
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }
    
}
