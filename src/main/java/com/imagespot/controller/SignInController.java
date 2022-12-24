package com.imagespot.controller;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.MainApplication;
import com.imagespot.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class SignInController implements Initializable {

    private double x, y;
    @FXML
    private Button btnSignIn;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnMinimize;
    @FXML
    private Hyperlink hlinkForgotPass;
    @FXML
    private Hyperlink hlinkSignIn;
    @FXML
    private Label logo;
    @FXML
    private PasswordField signInPass;
    @FXML
    private TextField signInUsername;
    @FXML
    private TextField signUpEmail;
    @FXML
    private Label signUpErr;
    @FXML
    private TextField signUpName;
    @FXML
    private PasswordField signUpPass;
    @FXML
    private TextField signUpUsername;
    @FXML
    private Label signInErr;
    @FXML
    private TabPane tabPane;

    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //agg caput a cosa serve init, in fxml nn agg miz o flag della funzione, ma cmq funziona
        redirectToLoginView();
    }

    @FXML
    private void signUpButtonOnAction() throws SQLException {

        if(signUpUsername.getText().isBlank() || signUpEmail.getText().isBlank()
                || signUpName.getText().isBlank() || signUpPass.getText().isBlank())
            signUpErr.setText("Fields can't be empty");

        else if(new UserDAOImpl().signup(signUpUsername.getText(), signUpName.getText(),
                signUpEmail.getText(), signUpPass.getText())){
            user = new User();
            user.setUsername(signUpUsername.getText());
            user.setName(signUpName.getText());
            user.setEmail(signUpEmail.getText());
            user.setPassword(signUpPass.getText());
            openAddInfoScene();
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            stage.close();
        }
        else signUpErr.setText("Credentials already exists");
    }

    @FXML
    private void signInButtonOnAction() throws SQLException {

        if(signInUsername.getText().isBlank() || signInPass.getText().isBlank())
            signInErr.setText("Fields can't be empty");
        else if(new UserDAOImpl().login(signInUsername.getText(), signInPass.getText())) {
            user = new UserDAOImpl().getUserInfo(signInUsername.getText());
            openHomeScene();
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            stage.close();
        }
        else signInErr.setText("Credentials are wrong :(");
    }

    @FXML
    private void hlinkForgotPass() {

        hlinkForgotPass.setText("¯\\_(ツ)_/¯");
        hlinkForgotPass.setStyle("-fx-underline: false");
    }

    /*
    * Le animazioni di windows nn funzionano*/
    @FXML
    private void closeButtonOnAction() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimizeButtonOnAction() {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    private void redirectToLoginView() {
        hlinkSignIn.setOnMouseClicked(ev -> {
            tabPane.getSelectionModel().select(0);
        });
    }

    private void openAddInfoScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-info-view.fxml"));
            Parent root = (Parent)loader.load();
            AddInfoController controller = loader.getController();
            controller.setUser(user);
            Scene scene = new Scene(root, 600, 483);
            Stage stage = new Stage();
            stage.setTitle("Add some info!");
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    private void openHomeScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-view.fxml"));
            Parent root = loader.load();
            HomeController controller = loader.getController();
            controller.initData(user);
            Scene scene = new Scene(root, 900, 550);
            Stage stage = new Stage();
            stage.setTitle("Imagespot - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    private void dragged(MouseEvent ev) {
        Stage stage = (Stage) logo.getScene().getWindow();
        stage.setX(ev.getScreenX() - x);
        stage.setY(ev.getScreenY() - y);
    }

    @FXML
    private void pressed(MouseEvent ev) {
        x = ev.getSceneX();
        y = ev.getSceneY();
    }
}