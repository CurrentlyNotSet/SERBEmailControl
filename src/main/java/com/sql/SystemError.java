/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.SystemErrorModel;
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
public class SystemError {
    
    /**
     * Gathers a list of errors based on type and count total of them
     * 
     * @return
     */
    public static List<SystemErrorModel> getErrorCounts() {
        List<SystemErrorModel> list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT exceptionType, COUNT(*) AS 'num' "
                    + "FROM SystemError "
                    + "WHERE timeOccurred >= CAST(CURRENT_TIMESTAMP AS DATE) "
                    + "GROUP BY exceptionType";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                SystemErrorModel item = new SystemErrorModel();
                item.setExceptionType(rs.getString("exceptionType") == null ? "" : rs.getString("exceptionType"));
                item.setNumber(rs.getInt("num"));
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
