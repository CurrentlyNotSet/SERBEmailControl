/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailOutModel;
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
public class EmailOut {
    public static List<EmailOutModel> getEmailOutQueue() {
        List<EmailOutModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM EmailOut";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmailOutModel item = new EmailOutModel();
                item.setId(rs.getInt("id"));
                item.setSection(rs.getString("Section"));
                item.setCaseYear(rs.getString("caseYear"));
                item.setCaseType(rs.getString("caseType"));
                item.setCaseMonth(rs.getString("caseMonth"));
                item.setCaseNumber(rs.getString("caseNumber"));
                item.setTo(rs.getString("to"));
                item.setFrom(rs.getString("from"));
                item.setCc(rs.getString("cc"));
                item.setBcc(rs.getString("bcc"));
                item.setSubject(rs.getString("subject"));
                item.setBody(rs.getString("body"));
                item.setUserID(rs.getInt("UserID"));
                list.add(item);
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

    public static void deleteEmailEntry(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM EmailOut WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            SlackNotification.sendNotification(ex.toString());
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
    }
}
