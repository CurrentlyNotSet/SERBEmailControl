/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.model.CasePartyModel;
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
public class CaseParty {

    public static List<CasePartyModel> getCasePartyList(String caseYear, String caseType, String caseMonth, String caseNumber) {
        List<CasePartyModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();

            String sql = "SELECT"
                    + " caseRelation,"
                    + " LastName,"
                    + " FirstName,"
                    + " MiddleInitial,"
                    + " jobTitle,"
                    + " companyName,"
                    + " Address1,"
                    + " Address2,"
                    + " City,"
                    + " stateCode,"
                    + " zipCode,"
                    + " phone1 FROM "
                    + " caseParty"
                    + " WHERE"
                    + " caseyear = ?"
                    + " AND casetype = ?"
                    + " AND casemonth = ?"
                    + " AND caseNumber = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, caseYear);
            ps.setString(2, caseType);
            ps.setString(3, caseMonth);
            ps.setString(4, caseNumber);
            rs = ps.executeQuery();

            while (rs.next()) {
                CasePartyModel item = new CasePartyModel();
                item.setCaseRelation(rs.getString("caseRelation") == null ? "" : rs.getString("caseRelation"));
                item.setLastName(rs.getString("LastName") == null ? "" : rs.getString("LastName"));
                item.setFirstName(rs.getString("FirstName") == null ? "" : rs.getString("FirstName"));
                item.setMiddleInitial(rs.getString("MiddleInitial") == null ? "" : rs.getString("MiddleInitial"));
                item.setJobTitle(rs.getString("jobTitle") == null ? "" : rs.getString("jobTitle"));
                item.setCompanyName(rs.getString("companyName") == null ? "" : rs.getString("companyName"));
                item.setAddress1(rs.getString("Address1") == null ? "" : rs.getString("Address1"));
                item.setAddress2(rs.getString("Address2") == null ? "" : rs.getString("Address2"));
                item.setCity(rs.getString("City") == null ? "" : rs.getString("City"));
                item.setStateCode(rs.getString("stateCode") == null ? "" : rs.getString("stateCode"));
                item.setZipcode(rs.getString("zipCode") == null ? "" : rs.getString("zipCode"));
                item.setPhone1(rs.getString("phone1") == null ? "" : rs.getString("phone1"));
                list.add(item);
            }
        } catch (SQLException ex) {
            if (ex.getCause() instanceof SQLServerException) {
                getCasePartyList(caseYear, caseType, caseMonth, caseNumber);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return list;
    }

}
