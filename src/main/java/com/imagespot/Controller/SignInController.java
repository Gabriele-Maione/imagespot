package com.imagespot.Controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.User;
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
    private void signUpButtonOnAction() {

        if (signUpUsername.getText().isBlank() || signUpEmail.getText().isBlank()
                || signUpName.getText().isBlank() || signUpPass.getText().isBlank())
            signUpErr.setText("Fields can't be empty");
        else if (!signUpEmail.getText().matches("[^@]+@[^@]+\\.[^@]+"))
            signUpErr.setText("Invalid email format");
        else if (signUpPass.getText().length() < 8)
            signUpErr.setText("Password must be at least 8 characters long");
        else
            checkCredentialsTask(signUpUsername.getText(), signUpEmail.getText(), signUpName.getText(), signUpPass.getText());
    }

    private void checkCredentialsTask(String username, String email, String name, String password) {
        final Task<Integer> checkCredentialsT = new Task<>() {
            @Override
            protected Integer call() {
                updateMessage("Loading...");
                return new UserDAOImpl().signup(username, email, name, password);
            }
        };
        new Thread(checkCredentialsT).start();
        signUpErr.textProperty().bind(checkCredentialsT.messageProperty());
        checkCredentialsT.setOnSucceeded(workerStateEvent -> {
            signUpErr.textProperty().unbind();
            switch(checkCredentialsT.getValue()) {
                case -1 -> signUpErr.setText("Username already exist");
                case -2 -> signUpErr.setText("Email already exist");
                case 0 -> signUpErr.setText("Username and email already exists");
                case 1 -> startNewUser();
            }
        });
    }

    private void startNewUser() {
        ViewFactory.getInstance().getUser().setUsername(signUpUsername.getText());
        ViewFactory.getInstance().getUser().setName(signUpName.getText());
        ViewFactory.getInstance().getUser().setEmail(signUpEmail.getText());
        ViewFactory.getInstance().showAddInfoWindow();
        Stage stage = (Stage) btnSignUp.getScene().getWindow();
        stage.close();
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
            protected Boolean call() {
                updateMessage("Loading...");
                if (new UserDAOImpl().login(signInUsername.getText(), signInPass.getText())) {
                    User user = new UserDAOImpl().getUserInfo(signInUsername.getText());

                    ViewFactory.getInstance().getUser().setUsername(user.getUsername());
                    ViewFactory.getInstance().getUser().setEmail(user.getEmail());
                    ViewFactory.getInstance().getUser().setName(user.getName());
                    ViewFactory.getInstance().getUser().setGender(user.getGender());
                    ViewFactory.getInstance().getUser().setBio(user.getBio());
                    if (user.getAvatar() != null)
                        ViewFactory.getInstance().getUser().setAvatar(user.getAvatar());
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
        hlinkSignIn.setOnMouseClicked(ev -> tabPane.getSelectionModel().select(0));
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