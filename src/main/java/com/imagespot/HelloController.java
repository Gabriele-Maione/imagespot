package com.imagespot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

    //DB STUFF DA SISTEMARE
    @FXML
    protected void signup() {
        Connection connection = getConnection();
        PreparedStatement statement;
        try {
            String sql = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username.getText());
            statement.setString(2, name.getText());
            statement.setString(3, email.getText());
            statement.setString(4, password.getText());
            statement.execute();

        } catch(Exception e) {e.printStackTrace();}
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}