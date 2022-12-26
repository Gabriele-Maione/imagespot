package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.MainApplication;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private GridPane postGrid;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView profilePic;
    @FXML
    private Button btnAddPhoto;
    @FXML
    private HBox hbYourGallery;
    @FXML
    private HBox hbBrowse;
    @FXML
    private BorderPane homePane;


    private User user;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();
        nameLabel.setText(user.getUsername());
        profilePic.setImage(new Image((user.getAvatar())));

        ViewFactory.getInstance().getClientSelectedMenuItem().addListener((observableValue, s, t1) -> {
            switch (t1) { //TODO: add cases
                case "YourGallery" -> homePane.setCenter(ViewFactory.getInstance().getYourGalleryView());
                case "Browse" -> homePane.setCenter(ViewFactory.getInstance().getBrowseView());
                default -> homePane.setCenter(ViewFactory.getInstance().getBrowseView());
            }
        });
        addListeners();
    }

    private void addListeners() { //TODO: add all listeners
        hbBrowse.setOnMouseClicked(event -> onBrowse());
        hbYourGallery.setOnMouseClicked(event -> onYourGallery());
    }

    private void onBrowse() {
        ViewFactory.getInstance().getClientSelectedMenuItem().set("Browse");
    }
    private void onYourGallery() {
        ViewFactory.getInstance().getClientSelectedMenuItem().set("YourGallery");
    }

    @FXML
    private void btnAddPhotoOnAction() {
        ViewFactory.getInstance().showAddPhotoWindow();
    }
}
