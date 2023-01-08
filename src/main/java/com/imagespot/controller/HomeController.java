package com.imagespot.controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.imagespot.Utils.Utils.crop;


public class HomeController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView profilePic;

    @FXML
    private Button btnAddPhoto;
    @FXML
    private HBox hbYourGallery;
    @FXML
    private HBox hbBrowse;
    @FXML
    private HBox hbFeed;
    @FXML
    private HBox hbFavorites;
    @FXML
    private BorderPane homePane;
    @FXML
    private Button btnMap;
    @FXML
    private TextField fldSearch;
    @FXML
    private MenuItem myProfileItem;
    @FXML
    private MenuItem settingsItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private AnchorPane searchButton;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();
        nameLabel.textProperty().bind(user.nameProperty());
        usernameLabel.setText("@" + user.getUsername());

        if(user.getAvatar() != null)
        profilePic.setImage(crop(user.getAvatar()));
        user.avatarProperty().addListener((ObservableValue<? extends Image> observable, Image oldVal, Image newVal) -> {
            if(newVal == null) profilePic.setImage(new Image(getClass().getResourceAsStream("/icons/bear_icon.png")));
            else profilePic.setImage(crop(user.getAvatar()));
        });

        homePane.setCenter(ViewFactory.getInstance().getBrowseView());

        addListeners();
    }


    private void addListeners() { //TODO: add all listeners

        //left bar buttons
        hbBrowse.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getBrowseView()));
        hbYourGallery.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getYourGalleryView()));
        hbFeed.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getFeedView()));
        hbFavorites.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getFavoritesView()));

        //search field
        fldSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) searchUser();
        });
        searchButton.setOnMouseClicked(event -> searchUser());

        //user's menu bar
        myProfileItem.setOnAction(actionEvent -> homePane.setCenter(ViewFactory.getInstance().getInstance()
                .getUserPage(ViewFactory.getInstance().getUser().getUsername())));
        settingsItem.setOnAction(actionEvent -> ViewFactory.getInstance().showSettingsWindow());
        logoutItem.setOnAction(actionEvent -> {
            ViewFactory.getInstance().closeSession();
            ViewFactory.getInstance().showAuthWindow();
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
