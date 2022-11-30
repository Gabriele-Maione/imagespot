package com.imagespot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.imagespot.Connection.ConnectionManager.getConnection;

public class HelloController {
    @FXML
    private Button btn_sign_in;
    @FXML
    private TextField username;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Label lbl_error;

    //DB STUFF DA SISTEMARE
    @FXML
    protected void signup() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement;
        String sql = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, username.getText());
        statement.setString(2, name.getText());
        statement.setString(3, email.getText());
        statement.setString(4, password.getText());
        statement.execute();
    }

    @FXML
    protected void onSignInClick() {
        try{
            signup();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Home");
            stage.setScene(scene);
            btn_sign_in.getScene().getWindow().hide();
            stage.show();
        } catch (IOException | SQLException e) {
            lbl_error.setVisible(true);
            throw new RuntimeException(e);
        }
    }
}