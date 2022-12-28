package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
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

public class AddInfoController implements Initializable {

    @FXML
    private Button btnAvatar;

    @FXML
    private Button btnSubmit;

    @FXML
    private ChoiceBox<String> cbGender;

    @FXML
    private TextArea bio;
    @FXML
    private Hyperlink hlinkSkip;

    @FXML
    private ImageView imgPreview;

    @FXML
    private Label welcomeLabel;

    private File avatar;

    private String[] gender = {"Male", "Female", "Not binary", "Prefer not say"};

    private User user;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();

        welcomeLabel.setText("Welcome, " + user.getName());
        cbGender.getItems().addAll(gender);
    }


    @FXML
    private void btnAvatarOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
            avatar = fc.showOpenDialog(imgPreview.getScene().getWindow());
            if(avatar != null)
            imgPreview.setImage(new Image((avatar.getAbsolutePath())));
    }

    @FXML
    private void submitBtnOnAction() throws SQLException, IOException {
        UserDAOImpl userDB = new UserDAOImpl();
        if(avatar != null)
            userDB.setAvatar(user.getUsername(), avatar);
        if(cbGender.getValue() != null)
            userDB.setGender(user.getUsername(), cbGender.getValue());
        if(!bio.getText().equals(""))
            userDB.setBio(user.getUsername(), bio.getText());

        Stage stage = (Stage)btnSubmit.getScene().getWindow();
        ViewFactory.getInstance().showHomeWindow();
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
}

