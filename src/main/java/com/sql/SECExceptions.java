/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.SECExceptionsModel;
import com.util.ExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;

/**
 *
 * @author Andrew
 */
public class SECExceptions {
    
    public static void insertException(SECExceptionsModel item){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO SECExceptions ("
                    + "className, "
                    + "methodName, "
                    + "exceptionType, "
                    + "exceptionNumber, "
                    + "exceptionDescription, "
                    + "timeOccurred "
                    + ") VALUES ("
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "GETUTCDATE())";
            ps = conn.prepareStatement(sql);
            ps.setString   (1, item.getClassName());
            ps.setString   (2, item.getMethodName());
            ps.setString   (3, item.getExceptionType());
            ps.setString   (4, item.getExceptionNumber());
            ps.setString   (5, item.getExceptionDescription());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }
    
    
}
