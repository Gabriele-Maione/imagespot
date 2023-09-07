package com.imagespot.Connection;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

    private static ConnectionManager instance;
    private final String url = "jdbc:postgresql://dpg-cj5sptqcn0vc7384d0sg-a.frankfurt-postgres.render.com:5432/imagespot";
    private final String driverName = "org.postgresql.Driver";
    private final String username = "imagespot";
    private final String password = "sxGcPx536GhTuwFeHtBwNEfqFM4lq4rL";
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
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Driver not found", ex);
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