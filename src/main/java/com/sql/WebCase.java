/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.model.WEBCaseModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author User
 */
public class WebCase {

    public static List<WEBCaseModel> getWebCaseList() {
        List<WEBCaseModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();

            String sql = "SELECT"
                    + " CMDSCase.CaseYear,"
                    + " CMDSCase.caseNumber,"
                    + " CMDSCase.casemonth,"
                    + " CMDSCase.casetype,"
                    + " Users.initials AS ALJ,"
                    + " CMDSCase.groupNumber"
                    + " FROM CMDSCase "
                    + " LEFT JOIN Users ON"
                    + " CMDSCase.aljID = Users.id"
                    + " WHERE (CMDSCase.caseYear > YEAR(GETDATE()) - 5) "
                    + " ORDER BY CMDSCase.caseyear, CMDSCase.caseNumber";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                WEBCaseModel item = new WEBCaseModel();
                item.setYear(rs.getString("caseyear") == null ? "" : rs.getString("caseyear"));
                item.setCaseSeqNumber(rs.getString("caseNumber") == null ? "" : rs.getString("caseNumber"));
                item.setMonth(rs.getString("casemonth") == null ? "" : rs.getString("casemonth"));
                item.setType(rs.getString("casetype") == null ? "" : rs.getString("casetype"));
                item.setALJ(rs.getString("ALJ") == null ? "" : rs.getString("ALJ"));
                item.setGroupNumber(rs.getString("groupNumber") == null ? "" : rs.getString("groupNumber"));
                list.add(item);
            }
        } catch (SQLException ex) {
            if (ex.getCause() instanceof SQLServerException) {
                getWebCaseList();
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return list;
    }

}
