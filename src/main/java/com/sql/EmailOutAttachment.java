/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailOutAttachmentModel;
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
public class EmailOutAttachment {
    
    /**
     * Gathers a list of attachments for a specific email address.
     * 
     * @param emailID Integer
     * @return List (EmailOutAttachmentModel) 
     */
    public static List<EmailOutAttachmentModel> getAttachmentsByEmail(int emailID) {
        List<EmailOutAttachmentModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM EmailOutAttachment WHERE emailOutID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, emailID);
            rs = ps.executeQuery();
            while (rs.next()) {
                EmailOutAttachmentModel item = new EmailOutAttachmentModel();
                item.setId(rs.getInt("id"));
                item.setEmailOutID(rs.getInt("emailOutID"));
                item.setFileName(rs.getString("fileName"));
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
     * Deletes attachment based off of email ID
     * 
     * @param id Integer
     */
    public static void deleteAttachmentsForEmail(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM EmailOutAttachment WHERE emailOutID = ?";
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
