package com.imagespot.Connection;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

    private static ConnectionManager instance;
    private final String url = "jdbc:postgresql://dpg-cef3kb1gp3jk7mgsl3b0-a.frankfurt-postgres.render.com:5432/imagespotdb";
    private final String driverName = "org.postgresql.Driver";
    private final String username = "oobdsquad";
    private final String password = "01ZpBQblnK9VysKAqQGyWWwMJrstJY41";
    private Connection con;

    private ConnectionManager() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to connect to the database");
                alert.setContentText("An error occurred while connecting to the database. Check your connection settings and try again.");
                alert.showAndWait();
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
        }
    }

    public Connection getConnection() {
        return con;
    }

    public static ConnectionManager getInstance() {

        try {
            if(instance == null || instance.getConnection().isClosed())
                instance = new ConnectionManager();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to connect to the database");
            alert.setContentText("An error occurred while connecting to the database. Check your connection settings and try again.");
            alert.showAndWait();
        }

        return instance;
    }

}
