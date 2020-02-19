/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.WebBoardOrderModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class WebBoardOrders {
    
    public static List<WebBoardOrderModel> getWebBoardOrdersList() {
        List<WebBoardOrderModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();

            String sql = "SELECT "
                    // Case Nuumber
                    + "(CMDSCase.caseYear + '-' + CMDSCase.caseType + '-' + CMDSCase.caseMonth + '-' + CMDSCase.caseNumber) AS CMDSCaseNumber, "
                    // View
                    + "('View') AS \"View\", "
                    // URL
                    + "('https://pbr.ohio.gov/static/PDF/Board_Orders/' + CMDSCase.caseYear + '/' + (RIGHT (CMDSCase.caseYear, 2) + '-' + CMDSCase.caseType + '-' + CMDSCase.caseMonth + '-' + CMDSCase.caseNumber) + '.pdf') AS URL, "
                    // AppellantName
                    + "(SELECT CaseParty.firstName + ' ' + CaseParty.lastName FROM CaseParty WHERE (CaseParty.caseYear = CMDSCase.caseYear AND CaseParty.caseType = CMDSCase.caseType AND CaseParty.caseMonth = CMDSCase.caseMonth AND CaseParty.caseNumber = CMDSCase.caseNumber) AND CaseParty.caseRelation = 'Appellant' ) AS AppellantName, "
                    // AppelleeName
                    + "((SELECT TOP 1 CASE WHEN CaseParty.companyName IS NOT NULL THEN REPLACE(CaseParty.companyName, ',',' ') ELSE ((CASE WHEN CaseParty.firstName IS NOT NULL THEN (REPLACE(CaseParty.firstName, ',',' ') + ' ') ELSE ('') END) + REPLACE(CaseParty.lastName, ',',' ')) END FROM CaseParty WHERE (CaseParty.caseYear = CMDSCase.caseYear AND CaseParty.caseType = CMDSCase.caseType AND CaseParty.caseMonth = CMDSCase.caseMonth AND CaseParty.caseNumber = CMDSCase.caseNumber) AND CaseParty.caseRelation = 'Appellee' )) AS AppelleeName, "
                    // Open Date
                    + "CONVERT(VARCHAR, CMDSCase.openDate, 23) AS OpenDate, "
                    // Board Order Date
                    + "CONVERT(VARCHAR, CMDSCase.mailedBO, 23) AS BoardOrderDate, "
                    // Result
                    + "CMDSCase.result AS Result "
                    + "FROM CMDSCase "
                    + "WHERE CMDSCase.mailedBO IS NOT NULL AND CMDSCase.mailedBO >= DATEADD(yy, -5, GETDATE()) "
                    + "ORDER BY CMDSCase.caseYear ASC, CMDSCase.caseMonth ASC, CMDSCase.caseNumber ASC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                WebBoardOrderModel item = new WebBoardOrderModel();
                item.setCMDSCaseNumber(rs.getString("CMDSCaseNumber") == null ? "" : rs.getString("CMDSCaseNumber"));
                item.setView(rs.getString("View") == null ? "" : rs.getString("View"));
                item.setURL(rs.getString("URL") == null ? "" : rs.getString("URL"));
                item.setAppellantName(rs.getString("AppellantName") == null ? "" : rs.getString("AppellantName"));
                item.setAppelleeName(rs.getString("AppelleeName") == null ? "" : rs.getString("AppelleeName"));
                item.setOpenDate(rs.getString("OpenDate") == null ? "" : rs.getString("OpenDate"));
                item.setBoardOrderDate(rs.getString("BoardOrderDate") == null ? "" : rs.getString("BoardOrderDate"));
                item.setResult(rs.getString("Result") == null ? "" : rs.getString("Result"));
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(WebCase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return list;
    }
    
}
