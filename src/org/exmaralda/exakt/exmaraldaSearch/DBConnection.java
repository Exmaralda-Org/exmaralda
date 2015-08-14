/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.exmaraldaSearch;

/**
 *
 * @author hanna...
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    //with some defaults
    private String driver = "com.mysql.jdbc.Driver";
    private String connection = null;
    private String DBDrv = "jdbc:mysql:";
    private static DBConnection cc = null;

    private DBConnection() throws ClassNotFoundException {

        Class.forName(driver);

    }

    public Connection getConnection(String connectionString, String user, String pwd) throws SQLException {
        if (connection == null) {
            connection = connectionString + "?useUnicode=true&characterEncoding=UTF-8";
        }
        Connection conn = DriverManager.getConnection(connection, user, pwd);
        return conn;
    }

    public Connection getConnection(String DBDrv, String DBURL, String DBTable, String user, String pwd) throws SQLException {

        if (connection == null) {
            connection = DBDrv + "//" + DBURL + "/" + DBTable + "?useUnicode=true&characterEncoding=UTF-8";
        }
        Connection conn = DriverManager.getConnection(connection, user, pwd);
        return conn;
    }

    public Connection getConnection(String DBURL, String DBTable, String user, String pwd) throws SQLException {
        if (connection == null) {
            connection = DBDrv + "//" + DBURL + "/" + DBTable + "?useUnicode=true&characterEncoding=UTF-8";
        }
        Connection conn = DriverManager.getConnection(connection, user, pwd);
        return conn;
    }

    public static DBConnection getInstance() throws ClassNotFoundException {
        if (cc == null) {
            cc = new DBConnection();
        }
        return cc;
    }
}
