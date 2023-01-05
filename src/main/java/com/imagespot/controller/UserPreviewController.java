package com.imagespot.controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class UserPreviewController implements Initializable {


    @FXML
    private ImageView avatar;

    @FXML
    private HBox hboxUser;

    @FXML
    private Label name;

    @FXML
    private Label username;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(User user) {

        this.user = user;
        if (user.getAvatar() != null) {
            avatar.setImage(user.getAvatar());
        }
        name.setText(user.getName());
        username.setText(user.getUsername());
    }

    public void userPreviewOnClick() {
        BorderPane borderPane = (BorderPane) hboxUser.getScene().getRoot();
        borderPane.setCenter(ViewFactory.getInstance().getUserPage(user.getUsername()));
    }

}
