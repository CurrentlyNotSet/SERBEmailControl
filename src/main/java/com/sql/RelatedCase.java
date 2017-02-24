/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.CaseTypeModel;
import com.model.EmailOutModel;
import com.model.RelatedCaseModel;
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
public class RelatedCase {
    
    public static List<RelatedCaseModel> getRelatedCases(String section) {
        List<RelatedCaseModel> list = new ArrayList();
        List<CaseTypeModel> casetypes = CaseType.getCaseTypesBySection(section);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int i = 0;
            conn = DBConnection.connectToDB();
            String sql = "SELECT * FROM RelatedCase WHERE LEN(relatedCaseNumber) = 16 ";
            
            if (!casetypes.isEmpty()) {
                sql += "AND (";

                while(i < casetypes.size()) {
                    sql += " Activity.caseType = ? OR";
                    i++;
                }

                sql = sql.substring(0, (sql.length() - 2)) + ")";
            }
            
            ps = conn.prepareStatement(sql);
                        
            int count = 0;
            for (Object casetype : casetypes) {
                count = count + 1;
                ps.setString(count, casetype.toString());
            }
            
            rs = ps.executeQuery();
            while (rs.next()) {
                String[] relatedCase = rs.getString("caseYear").split("-");
                
                if (relatedCase.length == 4){
                    RelatedCaseModel item = new RelatedCaseModel();
                    item.setCaseYear(rs.getString("caseYear"));
                    item.setCaseType(rs.getString("caseType"));
                    item.setCaseMonth(rs.getString("caseMonth"));
                    item.setCaseNumber(rs.getString("caseNumber"));
                    item.setRelatedCaseYear(relatedCase[0]);
                    item.setRelatedCaseType(relatedCase[1]);
                    item.setRelatedCaseMonth(relatedCase[2]);
                    item.setRelatedCaseNumber(relatedCase[3]);
                    list.add(item);
                }
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
