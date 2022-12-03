package com.imagespot.controller;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.MainApplication;
import com.imagespot.model.User;
import com.imagespot.DAOImpl.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.imagespot.Connection.ConnectionManager;

public class SignInController implements Initializable {
    public AnchorPane root;
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

    private double xOffset = 0;
    private double yOffset = 0;
    private UserDAOImpl userDAO;

    /*private final static Connection connection;   non avevo il coraggio di cancellarlo

    static {
        connection = getConnection();
    }


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
            statement.close();
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
    } */

    public SignInController() throws SQLException {
        userDAO = new UserDAOImpl();
    }
    @FXML
    protected void onSignInClick() {
        try{
            if(userDAO.signup(username.getText(), name.getText(), email.getText(), password.getText())){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(MainApplication.class.getResource("home-view.fxml"));//Da fare meglio
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Stage stage = new Stage();
                stage.setTitle("Home");
                stage.setScene(scene);
                HomeController controller = (HomeController)fxmlLoader.getController();
                User user = new User(username.getText(), name.getText(), email.getText(), password.getText());
                btn_sign_in.getScene().getWindow().hide();
                stage.show();
            }
            else{
                username.setStyle("-fx-border-color: red;");//test
                lbl_error.setText("User already exist!");
                lbl_error.setVisible(true);
            }
        } catch (IOException | SQLException e) {
            lbl_error.setVisible(true);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //grab your root here
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        //move around here
        root.setOnMouseDragged(event -> {

            root.getScene().getWindow().setX(event.getScreenX() - xOffset);
            root.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }
}
