package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
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
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private TextArea bio;

    @FXML
    private Button btnApply;

    @FXML
    private Button btnAvatar;

    @FXML
    private ChoiceBox<String> cbGender;

    @FXML
    private TextField fldName;

    @FXML
    private Hyperlink hlinkSkip;

    @FXML
    private ImageView imgPreview;

    @FXML
    private Label welcomeLabel;

    private File avatar;
    private boolean changedAvatarFlag = false;

    private String[] gender = {"Male", "Female", "Not binary", "Prefer not say"};

    private User user;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();

        if(user.getAvatar() != null) {
            System.out.println(user.getAvatar());

            imgPreview.setImage(user.getAvatar());
        }
        cbGender.getItems().addAll(gender);
        cbGender.setValue(user.getGender());
        fldName.setText(user.getName());
        bio.setText(user.getBio());

    }


    @FXML
    private void btnAvatarOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        avatar = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if(avatar != null) {
            imgPreview.setImage(new Image((avatar.getAbsolutePath())));
            changedAvatarFlag = true;
        }
    }


    private void updateInfoTask() {
        final Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Loading...");
                UserDAOImpl userDB = new UserDAOImpl();
                if(changedAvatarFlag) {
                    userDB.setAvatar(user.getUsername(), avatar);
                    ViewFactory.getInstance().getUser().setAvatar(new Image(avatar.getAbsolutePath()));
                }
                if(!cbGender.getValue().equals(user.getGender())) {
                    userDB.setGender(user.getUsername(), cbGender.getValue());
                    ViewFactory.getInstance().getUser().setGender(cbGender.getValue());
                }
                if(!bio.getText().equals(user.getBio())) {
                    userDB.setBio(user.getUsername(), bio.getText());
                    ViewFactory.getInstance().getUser().setBio(bio.getText());
                }
                return null;
            }
        };
        new Thread(updateTask).start();
        btnApply.textProperty().bind(updateTask.messageProperty());
        updateTask.setOnSucceeded(workerStateEvent -> {
            btnApply.textProperty().unbind();
            btnApply.setText("DONE!");

        });
    }

    @FXML
    private void closeButtonOnAction() {
        Stage stage = (Stage) hlinkSkip.getScene().getWindow();
        stage.close();
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

    @FXML
    private void submitBtnOnAction() {
        if(!changedAvatarFlag && fldName.getText().equals(user.getName())
        && bio.getText().equals(user.getBio()) && cbGender.getValue().equals(user.getGender()))
            btnApply.setText("NOTHING CHANGED");
        else
            updateInfoTask();
    }
}
