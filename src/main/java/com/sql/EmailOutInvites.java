/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailOutInvitesModel;
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
public class EmailOutInvites {

    /**
     * Get a list of all of the email invites awaiting to be sent.
     * 
     * @return List EmailOutInvitesModel
     */
    public static List<EmailOutInvitesModel> getQueuedEmailInvites() {
        List<EmailOutInvitesModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM EmailOutInvites";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmailOutInvitesModel item = new EmailOutInvitesModel();
                item.setId(rs.getInt("id"));
                item.setSection(rs.getString("Section") == null ? "" : rs.getString("Section"));
                item.setToAddress(rs.getString("TOaddress") == null ? "" : rs.getString("TOaddress"));
                item.setCcAddress(rs.getString("CCaddress") == null ? "" : rs.getString("CCaddress"));
                item.setEmailBody(rs.getString("emailBody") == null ? "" : rs.getString("emailBody"));
                item.setCaseNumber(rs.getString("caseNumber") == null ? "" : rs.getString("caseNumber"));
                item.setHearingType(rs.getString("hearingType") == null ? "" : rs.getString("hearingType"));
                item.setHearingRoomAbv(rs.getString("hearingRoomAbv") == null ? "" : rs.getString("hearingRoomAbv"));
                item.setHearingDescription(rs.getString("hearingDescription") == null ? "" : rs.getString("hearingDescription"));
                item.setHearingStartTime(rs.getTimestamp("hearingStartTime"));
                item.setHearingEndTime(rs.getTimestamp("hearingEndTime"));
                item.setEmailSubject(rs.getString("emailSubject") == null ? "" : rs.getString("emailSubject"));
                list.add(item);
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

    /**
     * Deletes email out invite from database after sending.
     * 
     * @param id Integer
     */
    public static void deleteEmailEntry(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM EmailOutInvites WHERE id = ?";
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
    
}
