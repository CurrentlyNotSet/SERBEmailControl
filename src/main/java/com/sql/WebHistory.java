/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.WebHistoryModel;
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
 * @author User
 */
public class WebHistory {

    public static List<WebHistoryModel> getWebHistoryList() {
        List<WebHistoryModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();

            String subSQL = "SELECT TOP 1 "
                    + "(ISNULL(CaseParty.lastName,'') + ', ' + ISNULL(CaseParty.firstname,'')) "
                    + "FROM CaseParty "
                    + "WHERE CaseParty.caseRelation = 'Appellant' "
                    + "AND (CaseParty.caseYear = Activity.caseYear "
                    + "AND CaseParty.caseType = Activity.caseType "
                    + "AND CaseParty.caseMonth = Activity.caseMonth "
                    + "AND CaseParty.caseNumber = Activity.caseNumber)";

            String sql = "SELECT "
                    + "RIGHT(Activity.CaseYear, 2) AS caseYear, "
                    + "LEFT(Activity.CaseMonth, 2) AS caseMonth, "
                    + "Activity.CaseNumber, "
                    + "LEFT(Activity.caseType, 3) AS caseType, "
                    + "CONVERT(char(10), Activity.date,126) as date, "
                    + "LEFT((" + subSQL + "), 39) AS Appellant, "
                    + "LEFT(Activity.action + ' ' + ISNULL(Activity.comment,''), 54) AS action "
                    + "FROM "
                    + "Activity INNER JOIN CaseType ON "
                    + "Activity.caseType = CaseType.caseType "
                    + "WHERE "
                    + "((Activity.action NOT LIKE '%CREATED ON%' "
                    + "AND Activity.action NOT LIKE '%Generated%' "
                    //+ "AND Activity.action NOT LIKE '%Case was Filed and Started%' " <-- removed at JB request R3-009
                    + "AND Activity.action NOT LIKE 'Added %' "
                    + "AND Activity.action NOT LIKE 'Set %' "
                    + "AND Activity.action NOT LIKE 'Changed %' "
                    + "AND Activity.action NOT LIKE 'Ebody %' "
                    + "AND Activity.action NOT LIKE 'Removed %' "
                    + "AND Activity.type != 'I' "
                    + "AND Activity.type != 'L' "
                    + "AND Activity.type != 'U' "
                    + "AND Activity.type != 'W') "
                    + "OR (Activity.type = 'A' AND Activity.action LIKE '%initial case preparation%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%initial case preparation%') "
                    + "OR (Activity.type = 'E' AND Activity.action LIKE '%Procedural Order & Questionnaire Mailed%') "
                    + "OR (Activity.type = 'O' AND Activity.action LIKE '%Procedural Order & Questionnaire Mailed%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%Pre Hearing Scheduled%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%Record Hearing Scheduled%') "
                    + "OR (Activity.type = 'E' AND Activity.action LIKE '%Procedural Order Mailed%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%Status Conference Scheduled%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%Notice Of Appeal Filed%') "
                    + "OR (Activity.type = 'A' AND Activity.action LIKE '%Notice of Appearance%') "
                    + "OR (Activity.type = 'E' AND Activity.action LIKE '%Procedural Order Mailed%') "
                    + "OR (Activity.type = 'A' AND Activity.action LIKE '%Notice of Withdrawal Of Counsel%') "
                    + "OR (Activity.type = 'Z' AND Activity.action LIKE '%Miscellaneous Witness And Documents List%')) "
                    + "AND (Activity.caseYear > YEAR(GETDATE()) - 5) "
                    + "AND Activity.active = 1 "
                    + "AND CaseType.section = 'CMDS' "
                    + "ORDER BY Activity.caseyear, Activity.caseNumber";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                WebHistoryModel item = new WebHistoryModel();
                item.setCaseYear(rs.getString("CaseYear") == null ? "" : rs.getString("CaseYear"));
                item.setCaseType(rs.getString("caseType") == null ? "" : rs.getString("caseType"));
                item.setCaseMonth(rs.getString("caseMonth") == null ? "" : rs.getString("caseMonth"));
                item.setCaseNumber(rs.getString("CaseNumber") == null ? "" : rs.getString("CaseNumber"));
                item.setAppellant(rs.getString("Appellant") == null ? "" : rs.getString("Appellant"));
                item.setEntryDate(rs.getString("date") == null ? "" : rs.getString("date"));
                item.setEntryDescription(rs.getString("action") == null ? "" : rs.getString("action"));
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(WebHistory.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return list;
    }
}
