/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.CaseTypeModel;
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
public class CaseType {
    
    /**
     * Gathers a list of active case types for finding the proper section based 
     * on the case number.
     * 
     * @return List CaseTypeModel
     */
    public static List<CaseTypeModel> getCaseTypes() {
        List<CaseTypeModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM CaseType WHERE active = 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                CaseTypeModel item = new CaseTypeModel();
                item.setId(rs.getInt("id"));
                item.setActive(rs.getBoolean("active"));
                item.setSection(rs.getString("Section"));
                item.setCaseType(rs.getString("caseType"));
                item.setDescription(rs.getString("Description"));
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
     * Gathers a list of active case types for finding the proper section based 
     * on the case number.
     * 
     * @param section For which section the method is currently processing
     * @return List CaseTypeModel
     */
    public static List<CaseTypeModel> getCaseTypesBySection(String section) {
        List<CaseTypeModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM CaseType WHERE active = 1 AND section = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, section);
            rs = ps.executeQuery();
            while (rs.next()) {
                CaseTypeModel item = new CaseTypeModel();
                item.setId(rs.getInt("id"));
                item.setActive(rs.getBoolean("active"));
                item.setSection(rs.getString("Section"));
                item.setCaseType(rs.getString("caseType"));
                item.setDescription(rs.getString("Description"));
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
}
