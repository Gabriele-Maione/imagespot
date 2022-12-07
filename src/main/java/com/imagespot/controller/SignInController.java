package com.imagespot.controller;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.imagespot.DAOImpl.UserDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


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
            signUpErr.setText("User Registered, but main page is not ready lulz");
        }
        else signUpErr.setText("Credentials already exists");
    }

    @FXML
    private void signInButtonOnAction() throws SQLException {

        if(signInUsername.getText().isBlank() || signInPass.getText().isBlank())
            signInErr.setText("Fields can't be empty");
        else if(new UserDAOImpl().login(signInUsername.getText(), signInPass.getText())) {
            signInErr.setText("Well cum");
        }
        else signInErr.setText("Credentials are wrong :(");
    }

    @FXML
    private void hlinkForgotPass() {

        hlinkForgotPass.setText("¯\\_(ツ)_/¯");
        hlinkForgotPass.setStyle("-fx-underline: false");
    }

    /*
    * Le animazioni di windows nn funzionano ed ho fatto
    * il design di tutti i bottoni quindi ora è tutto bruttissimo*/
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