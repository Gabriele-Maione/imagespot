package com.imagespot.Connection;
import java.sql.*;

public class ConnectionManager {

    private static ConnectionManager instance;
    private final String url = "jdbc:postgresql://dpg-cef3kb1gp3jk7mgsl3b0-a.frankfurt-postgres.render.com:5432/imagespotdb";
    private final String driverName = "org.postgresql.Driver";
    private final String username = "oobdsquad";
    private final String password = "01ZpBQblnK9VysKAqQGyWWwMJrstJY41";
    private Connection con;

    private ConnectionManager() throws SQLException {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                //log exception
                System.out.println("Failed to create the database connection");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
        }
    }

    public Connection getConnection() {
        return con;
    }

    public static ConnectionManager getInstance() throws SQLException {

        if(instance == null || instance.getConnection().isClosed())
            instance = new ConnectionManager();

        return instance;
    }

}
