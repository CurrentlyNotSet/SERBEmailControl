/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sql;

import com.util.DBCInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Andrew
 */
public class DBConnection {

    public static Connection connectToDB() {
        Connection conn = null;
        int nbAttempts = 0;
        while (true) {
            try {
                Class.forName(DBCInfo.getDBdriver());
                conn = DriverManager.getConnection(DBCInfo.getDBurl(), DBCInfo.getDBusername(), DBCInfo.getDBpassword());
                break;
            } catch (ClassNotFoundException | SQLException e) {
                nbAttempts++;
                System.out.println();
                if (nbAttempts == 2) {
                    System.out.println("<html><center>Unable to connect to server.<br><br>"
                            + "Please verify network connection and press OK to try again.</center></html>");
                            }
                try {
                    Thread.sleep(3000);
                } catch (Exception exi) {
                    System.err.println(exi.getMessage());
                }
                if (nbAttempts == 3) {
                    System.out.println("<html><center>Unable to connect to server.<br><br>"
                            + "Information could not be saved. The system will now exit.</center></html>");
                    System.exit(0);
                }
            }
        }
        return conn;
    }
}
