/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.ActivityModel;
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
public class Activity {
    
    public static List getFilesToStamp() {
        List<ActivityModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM Activity WHERE awaitingTimestamp = 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                ActivityModel type = new ActivityModel();
                type.setId(rs.getInt("id"));
                type.setCaseYear(rs.getString("caseYear"));
                type.setCaseType(rs.getString("caseType"));
                type.setCaseMonth(rs.getString("caseMonth"));
                type.setCaseNumber(rs.getString("caseNumber"));
                type.setDate(rs.getTimestamp("date"));
                type.setFilePath(rs.getString("fileName"));
                list.add(type);
            }
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return list;
    }
    
    public static void markEntryStamped(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "UPDATE Activity SET awaitingTimestamp = 0 WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
    
}
