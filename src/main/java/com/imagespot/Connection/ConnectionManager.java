package com.imagespot.Connection;
import java.sql.*;

public class ConnectionManager {
    private static final String url = "jdbc:postgresql://ec2-52-212-228-71.eu-west-1.compute.amazonaws.com:5432/d4uten7a2rimt9?searchpath=Imagespot";
    private static final String driverName = "org.postgresql.Driver";
    private static final String username = "hlzqcandbxakhx";
    private static final String password = "a61a705efd67e4955bc54903f56ce61aec39a1d5474a353300ad691581762139";
    private static Connection con;

    public static Connection getConnection() {
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
        return con;
    }
}
