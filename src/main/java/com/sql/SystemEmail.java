/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.SystemEmailModel;
import com.util.ExceptionHandler;
import com.util.Global;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class SystemEmail {
    
    /**
     * Gathers active email accounts for sending or receiving. 
     * 
     * @return
     */
    public static boolean loadEmailConnectionInformation() {
        List<SystemEmailModel> systemEmailList = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM SystemEmail WHERE active = 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                SystemEmailModel row = new SystemEmailModel();
                row.setId(rs.getInt("id"));
                row.setActive(rs.getInt("active"));
                row.setSection(rs.getString("section"));
                row.setEmailAddress(rs.getString("emailAddress"));
                row.setUsername(rs.getString("username"));
                row.setPassword(rs.getString("password"));
                row.setIncomingURL(rs.getString("incomingURL"));
                row.setIncomingPort(rs.getInt("incomingPort"));
                row.setIncomingProtocol(rs.getString("incomingProtocol"));
                row.setIncomingFolder(rs.getString("incomingFolder"));
                row.setOutgoingURL(rs.getString("outgoingURL"));
                row.setOutgoingPort(rs.getInt("outgoingPort"));
                row.setOutgoingProtocol(rs.getString("outgoingProtocol"));
                row.setOutgoingFolder(rs.getString("outgoingFolder"));
                systemEmailList.add(row);
            }
            Global.setSystemEmailParams(systemEmailList);
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
            return false;
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return true;
    }
    
}
