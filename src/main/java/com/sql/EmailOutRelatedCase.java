/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.EmailOutModel;
import com.model.EmailOutRelatedCaseModel;
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
public class EmailOutRelatedCase {

    public static List<EmailOutRelatedCaseModel> getRelatedCases(EmailOutModel eml) {
        List<EmailOutRelatedCaseModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM EmailOutRelatedCase WHERE emailId = ? ";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, eml.getId());

            rs = ps.executeQuery();
            while (rs.next()) {
                    EmailOutRelatedCaseModel item = new EmailOutRelatedCaseModel();
                    item.setId(rs.getInt("id"));
                    item.setEmailOutId(rs.getInt("emailOutId"));
                    item.setCaseYear(rs.getString("caseYear"));
                    item.setCaseType(rs.getString("caseType"));
                    item.setCaseMonth(rs.getString("caseMonth"));
                    item.setCaseNumber(rs.getString("caseNumber"));
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
     * @param emailOutId Integer
     */
    public static void deleteEmailOutRelatedForEmail(int emailOutId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM EmailOutRelatedCase WHERE emailOutID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, emailOutId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }

}
