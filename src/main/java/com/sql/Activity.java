/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.ActivityModel;
import com.util.ExceptionHandler;
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
                type.setFileName(rs.getString("fileName"));
                list.add(type);
            }
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
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
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
    
    public static void insertActivity(ActivityModel act){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO Activity ("
                    + "caseYear, "        //1
                    + "caseType, "        //2
                    + "caseMonth, "       //3
                    + "caseNumber, "      //4
                    + "userID, "          //5
                    + "date, "            //6
                    + "action, "          //7
                    + "fileName, "        //8
                    + "[from], "          //9 
                    + "[to], "            //10
                    + "type, "            //11
                    + "comment, "         //12
                    + "redacted, "        //13
                    + "awaitingTimestamp "//14
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
                    + "?, " //11
                    + "?, " //12
                    + "?, " //13
                    + "?)"; //14
            ps = conn.prepareStatement(sql);
            ps.setString   ( 1, act.getCaseYear());
            ps.setString   ( 2, act.getCaseType());
            ps.setString   ( 3, act.getCaseMonth());
            ps.setString   ( 4, act.getCaseNumber());
            ps.setString   ( 5, act.getUserID());
            ps.setTimestamp( 6, act.getDate());
            ps.setString   ( 7, act.getAction());
            ps.setString   ( 8, act.getFileName());
            ps.setString   ( 9, act.getFrom());
            ps.setString   (10, act.getTo());
            ps.setString   (11, act.getType());
            ps.setString   (12, act.getComment());
            ps.setInt      (13, act.getRedacted());
            ps.setInt      (14, act.getAwaitingTimestamp());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
}
