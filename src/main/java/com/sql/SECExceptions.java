/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.model.SECExceptionsModel;
import com.model.SystemErrorModel;
import com.util.ExceptionHandler;
import com.util.Global;
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
public class SECExceptions {
    
    /**
     * Inserts an exception to the database
     * 
     * @param item SECExceptionsModel
     * @return boolean
     */
    public static boolean insertException(SECExceptionsModel item){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "INSERT INTO SECExceptions ("
                    + "className, "
                    + "methodName, "
                    + "exceptionType, "
                    + "exceptionDescrption, "
                    + "timeOccurred "
                    + ") VALUES ("
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "GETDATE())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, item.getClassName());
            ps.setString(2, item.getMethodName());
            ps.setString(3, item.getExceptionType());
            ps.setString(4, item.getExceptionDescription());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return true;
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
        return false;
    }
        
    /**
     * Removes old exception based off of a global exception date timeframe
     */
    public static void removeOldExceptions(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "DELETE FROM SECExceptions WHERE "
                    + "timeOccurred < dateadd(" + Global.getExceptionTimeFrame() 
                    + ",-" + Global.getExceptionTimeAmount() + ", getdate())";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }
    
    /**
     * Gets a count of errors where the description text matches. 
     * This is to eliminate the repeat of entries from the application looping
     * 
     * @param description String
     * @return Integer count
     */
    public static int getExistingException(String description) {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.connectToDB();
            String sql = "SELECT COUNT(*) AS num FROM SECExceptions WHERE "
                    + "timeOccurred >= CAST(CURRENT_TIMESTAMP AS DATE) AND exceptionDescrption = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, description);
            
            rs = ps.executeQuery();
            while(rs.next()){
                count = rs.getInt("num");
            }
        } catch (SQLException ex) {
            ExceptionHandler.Handle(ex);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(rs);
        }
        return count;
    }
    
    /**
     *  Gathers a list of errors based on type and count total of them.
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
                    + "FROM SECExceptions "
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
