/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.util.ExceptionHandler;
import com.util.Global;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andrew
 */
public class Audit {

    /**
     * Removes old audits based on specific time frame.
     */
    public static void removeOldAudits(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM Audit WHERE "
                    + "date < dateadd(" + Global.getAuditTimeFrame()
                    + ", -" + Global.getAuditTimeAmount() + ", getdate())";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }

    /**
     * Adds an entry to the audit table
     * @param action performed action to be stored
     */
    public static void addAuditEntry(String action) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {

            conn = DBConnection.connectToDB();

            String sql = "INSERT INTO Audit VALUES"
                    + "(?,?,?)";

            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, 0);
            ps.setString(3, action == null ? "MISSING ACTION" : StringUtils.left(action, 255));

            ps.executeUpdate();
        } catch (SQLException ex) {
            if(ex.getCause() instanceof SQLServerException) {
                addAuditEntry(action);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }

}
