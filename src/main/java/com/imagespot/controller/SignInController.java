package com.imagespot.controller;

import com.imagespot.MainApplication;
import com.imagespot.model.User;
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
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.imagespot.Connection.ConnectionManager.getConnection;

public class SignInController {
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

    private final static Connection connection;

    static {
        connection = getConnection();
    }

    //DB STUFF DA SISTEMARE
    @FXML
    protected boolean signup() throws SQLException {
        if(!userExist()){
            PreparedStatement statement;
            String sql = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username.getText());
            statement.setString(2, name.getText());
            statement.setString(3, email.getText());
            statement.setString(4, password.getText());
            statement.execute();
            return true;
        }
        else return false;
    }

    private boolean userExist() throws SQLException{
        PreparedStatement statement;
        String sql = "SELECT count(*) FROM account WHERE Username = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, username.getText());
        ResultSet  rs =  statement.executeQuery();

        if(rs.next()){
            return rs.getInt(1) == 1;
        }
        return false;
    }

    @FXML
    protected void onSignInClick() {
        try{
            if(signup()){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(MainApplication.class.getResource("home-view.fxml"));//Da fare meglio
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Stage stage = new Stage();
                stage.setTitle("Home");
                stage.setScene(scene);
                HomeController controller = (HomeController)fxmlLoader.getController();
                User user = new User(username.getText(), name.getText(), email.getText(), password.getText());
                controller.setUser(user);
                btn_sign_in.getScene().getWindow().hide();
                stage.show();
            }
            else{
                lbl_error.setText("User already exist!");
                lbl_error.setVisible(true);
            }
        } catch (IOException | SQLException e) {
            lbl_error.setVisible(true);
            throw new RuntimeException(e);
        }
    }
}
