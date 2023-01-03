package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private GridPane postGrid;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView profilePic;
    @FXML
    private ImageView searchIcon;
    @FXML
    private Button btnAddPhoto;
    @FXML
    private HBox hbYourGallery;
    @FXML
    private HBox hbBrowse;
    @FXML
    private HBox hbFeed;
    @FXML
    private BorderPane homePane;
    @FXML
    private Button btnMap;
    @FXML
    private TextField fldSearch;


    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();
        nameLabel.setText(user.getUsername());

        if (user.getAvatar() != null)
            profilePic.setImage(new Image((user.getAvatar())));
        homePane.setCenter(ViewFactory.getInstance().getBrowseView());

        addListeners();
    }


    private void addListeners() { //TODO: add all listeners
        hbBrowse.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getBrowseView()));
        hbYourGallery.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getYourGalleryView()));
        hbFeed.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getFeedView()));
        searchIcon.setOnMouseClicked(event -> searchUser());
        fldSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) searchUser();
        });
    }


    private void searchUser() {
        if (fldSearch.getText().trim().isBlank()) {
            fldSearch.setPromptText("FIELD IS EMPTY");
        } else homePane.setCenter(ViewFactory.getInstance().getSearchedUsers(fldSearch.getText().trim()));
    }

    @FXML
    private void btnAddPhotoOnAction() {
        ViewFactory.getInstance().showAddPhotoWindow();
    }

}
