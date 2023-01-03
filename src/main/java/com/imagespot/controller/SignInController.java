package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


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
    private void onKeyEnterPressed(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)){
            try {
                if(tabPane.getSelectionModel().getSelectedIndex() == 0)
                    signInButtonOnAction();
                else
                    signUpButtonOnAction();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void signUpButtonOnAction() throws SQLException {

        if(signUpUsername.getText().isBlank() || signUpEmail.getText().isBlank()
                || signUpName.getText().isBlank() || signUpPass.getText().isBlank())
            signUpErr.setText("Fields can't be empty");

        else if(new UserDAOImpl().signup(signUpUsername.getText(), signUpName.getText(),
                signUpEmail.getText(), signUpPass.getText())) {
            ViewFactory.getInstance().getUser().setUsername(signUpUsername.getText());
            ViewFactory.getInstance().getUser().setName(signUpName.getText());
            ViewFactory.getInstance().getUser().setEmail(signUpEmail.getText());
            ViewFactory.getInstance().getUser().setPassword(signUpPass.getText());
            ViewFactory.getInstance().showAddInfoWindow();
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            stage.close();
        } else signUpErr.setText("Credentials already exists");
    }

    @FXML
    private void signInButtonOnAction() throws SQLException {

        if (signInUsername.getText().isBlank() || signInPass.getText().isBlank())
            signInErr.setText("Fields can't be empty");
        else checkUserSignInTask();
    }

    public void checkUserSignInTask() {
        final Task<Boolean> userCheck = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                updateMessage("Loading...");
                if (new UserDAOImpl().login(signInUsername.getText(), signInPass.getText())) {
                    new UserDAOImpl().getUserInfo(signInUsername.getText());
                    return true;
                }
                else updateMessage("Credentials are wrong :(");
                return false;
            }
        };
        signInErr.textProperty().bind(userCheck.messageProperty());
        new Thread(userCheck).start();
        userCheck.setOnSucceeded(workerStateEvent -> {
            if(userCheck.getValue()) {
                ViewFactory.getInstance().showHomeWindow();
                Stage stage = (Stage)signInErr.getScene().getWindow();
                stage.close();
            }
        });
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