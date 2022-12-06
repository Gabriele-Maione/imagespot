package com.imagespot.Connection;
import java.sql.*;

public class ConnectionManager {

    private static ConnectionManager instance;
    private final String url = "jdbc:postgresql://ec2-52-212-228-71.eu-west-1.compute.amazonaws.com:5432/d4uten7a2rimt9";
    private final String driverName = "org.postgresql.Driver";
    private final String username = "hlzqcandbxakhx";
    private final String password = "a61a705efd67e4955bc54903f56ce61aec39a1d5474a353300ad691581762139";
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
