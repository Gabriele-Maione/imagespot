package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.List;
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
    private BorderPane homePane;
    @FXML
    private TextField fldSearch;
    @FXML
    private FlowPane followedUserList;
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
        user.avatarProperty().addListener((ObservableValue<? extends Image> observable, Image oldVal, Image newVal) ->
                profilePic.setImage( (newVal == null) ? new Image(getClass().getResourceAsStream("/icons/bear_icon.png")) : crop(user.getAvatar())));

        homePane.setCenter(ViewFactory.getInstance().getBrowseView());
        getFollowedUsersTask(user.getUsername());
        addListeners();
    }


    private void addListeners() { //TODO: add all listeners

        //left bar buttons
        hbBrowse.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getBrowseView()));
        hbYourGallery.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getYourGalleryView()));
        hbFeed.setOnMouseClicked(event -> homePane.setCenter(ViewFactory.getInstance().getFeedView()));

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

    private void getFollowedUsersTask(String username){
        Task<List<User>> followedUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                return new UserDAOImpl().getFollowedUsers(username);
            }
        };

        new Thread(followedUsersTask).start();
        followedUsersTask.setOnSucceeded(workerStateEvent -> {

            for (User user : followedUsersTask.getValue()) {
                HBox postBox = ViewFactory.getInstance().getFollowedUserPreview(user);
                followedUserList.getChildren().add(postBox);
            }
        });

    }

    @FXML
    private void btnAddPhotoOnAction() {
        ViewFactory.getInstance().showAddPhotoWindow();
    }

}
