/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.SystemEmailModel;
import com.util.Global;
import com.util.SlackNotification;
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
    
    public static void loadEmailConnectionInformation() {
        List<SystemEmailModel> incomingList = new ArrayList();
        List<SystemEmailModel> outgoingList = new ArrayList();
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
                row.setIO(rs.getString("inout"));
                row.setSection(rs.getString("section"));
                row.setProtocol(rs.getString("protocol"));
                row.setEmailAddress(rs.getString("emailAddress"));
                row.setUrl(rs.getString("url"));
                row.setUser(rs.getString("username"));
                row.setPass(rs.getString("password"));
                row.setFolder(rs.getString("folder"));

                switch (rs.getString("inout")) {
                    case "IN":
                        incomingList.add(row);
                        break;
                    case "OUT":
                        outgoingList.add(row);
                        break;
                }
            }
            Global.setIncomingEmailParams(incomingList);
            Global.setOutgoingEmailParams(outgoingList);
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
    }
    
}
