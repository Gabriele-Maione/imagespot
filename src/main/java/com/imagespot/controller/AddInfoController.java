package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AddInfoController implements Initializable {

    @FXML
    private ImageView avatarPreview;

    @FXML
    private TextArea bio;

    @FXML
    private Button btnAvatar;

    @FXML
    private ChoiceBox<String> cbGender;

    @FXML
    private TextField fldCustom;

    @FXML
    private Hyperlink hlinkSkip;

    @FXML
    private Button submit;

    @FXML
    private Label welcomeLabel;

    private File avatar;

    private final String[] gender = {"Male", "Female", "Not binary", "Prefer not say", "Custom"};

    private User user;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();
        Utils.setAvatarRounde(avatarPreview);

        welcomeLabel.setText("Welcome, " + user.getName());
        choiceBoxInit();
    }

    public void choiceBoxInit() {
        cbGender.getItems().addAll(gender);
        cbGender.setValue(user.getGender());
        cbGender.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (cbGender.getValue().equals("Custom")) {
                        fldCustom.setVisible(true);
                        cbGender.valueProperty().bind(fldCustom.textProperty());
                    } else if(!newValue.equals(fldCustom.getText())){
                        cbGender.valueProperty().unbind();
                        cbGender.setValue(newValue);
                        fldCustom.setVisible(false);
                        cbGender.valueProperty().unbind();
                    }
                });
    }

    @FXML
    private void uploadBtnOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
            avatar = fc.showOpenDialog(avatarPreview.getScene().getWindow());
            if(avatar != null)
                avatarPreview.setImage((new Image((avatar.getAbsolutePath()))));
    }

    @FXML
    private void submitBtnOnAction() {

        submitTask();
    }

    private void submitTask() {
        final Task<Void> submitTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UserDAOImpl userDB = new UserDAOImpl();
                if(avatar != null)
                    userDB.setAvatar(user.getUsername(), avatar);
                if(cbGender.getValue() != null)
                    userDB.setGender(user.getUsername(), cbGender.getValue());
                if(!bio.getText().equals(""))
                    userDB.setBio(user.getUsername(), bio.getText());
                return null;
            }
        };
        new Thread(submitTask).start();
        submitTask.setOnSucceeded(workerStateEvent -> {
            Stage stage = (Stage)submit.getScene().getWindow();
            stage.close();
            try {
                new UserDAOImpl().getUserInfo(user.getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ViewFactory.getInstance().showHomeWindow();
        });
    }

    @FXML
    private void skipBtnOnAction() {
        Stage stage = (Stage) hlinkSkip.getScene().getWindow();
        stage.close();
        ViewFactory.getInstance().showHomeWindow();
    }

    @FXML
    private void dragged(MouseEvent ev) {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setX(ev.getScreenX() - x);
        stage.setY(ev.getScreenY() - y);
    }

    @FXML
    private void pressed(MouseEvent ev) {
        x = ev.getSceneX();
        y = ev.getSceneY();
    }
}

