/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.util.DBCInfo;
import com.util.StringUtilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Andrew
 */
public class DBConnection {

    /**
     * Gets the connection for the default database. 
     */
    public static Connection connectToDB() {
        Connection conn = null;
        int nbAttempts = 0;
        while (true) {
            try {
                Class.forName(DBCInfo.getDBdriver());
                conn = DriverManager.getConnection(DBCInfo.getDBurl() + DBCInfo.getDBname(), DBCInfo.getDBusername(), DBCInfo.getDBpassword());
                break;
            } catch (ClassNotFoundException | SQLException e) {
                nbAttempts++;
                if (nbAttempts > 0) {
                    System.out.println(StringUtilities.currentTime()
                            + " - Unable to connect to server. Trying again shortly.");
                }
                try {
                    System.out.println("Sleeping for: " + 3000 * nbAttempts + "ms");
                    Thread.sleep(3000 * nbAttempts);
                } catch (InterruptedException exi) {
                    System.err.println(exi.getMessage());
                }
            }
        }
        return conn;
    }
    
    /**
     * Gets the connection for backing up the database.
     * 
     * @return
     */
    public static Connection connectToDBforBackup() {
        Connection conn = null;
        int nbAttempts = 0;
        while (true) {
            try {
                Class.forName(DBCInfo.getDBdriver());
                conn = DriverManager.getConnection(DBCInfo.getDBurl(), DBCInfo.getDBusername(), DBCInfo.getDBpassword());
                break;
            } catch (ClassNotFoundException | SQLException e) {
                nbAttempts++;
                if (nbAttempts > 0) {
                    System.out.println(StringUtilities.currentTime()
                            + " - Unable to connect to server. Trying again shortly.");
                }
                try {
                    System.out.println("Sleeping for: " + 3000 * nbAttempts + "ms");
                    Thread.sleep(3000 * nbAttempts);
                } catch (InterruptedException exi) {
                    System.err.println(exi.getMessage());
                }
            }
        }
        return conn;
    }
}
