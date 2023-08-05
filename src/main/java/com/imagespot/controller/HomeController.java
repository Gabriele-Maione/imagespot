package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.User;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import static com.imagespot.Utils.Utils.crop;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class HomeController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView profilePic;
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
    private MenuItem addPhotoItem;
    @FXML
    private MenuItem addCollectionItem;
    @FXML
    private StackPane searchButton;
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = ViewFactory.getInstance().getUser();

        nameLabel.textProperty().bind(user.nameProperty());
        usernameLabel.setText("@" + user.getUsername());

        setAvatarRounde(profilePic);
        if(user.getAvatar() != null){
            profilePic.setImage(user.getAvatar());
        }
        user.avatarProperty().addListener((ObservableValue<? extends Image> observable, Image oldVal, Image newVal) ->
                profilePic.setImage( (newVal == null) ? new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/bear_icon.png"))) : crop(user.getAvatar())));

        homePane.setCenter(ViewFactory.getInstance().getBrowseView());

        getFollowedUsersTask(user.getUsername());
        addListeners();
    }


    private void addListeners() {
        //search field
        fldSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) searchUser();
        });
        searchButton.setOnMouseClicked(event -> searchUser());

        //create button
        addPhotoItem.setOnAction(event -> ViewFactory.getInstance().showAddPhotoWindow());
        addCollectionItem.setOnAction(event -> ViewFactory.getInstance().showAddCollectionWindow());
        //user's menu bar
        myProfileItem.setOnAction(actionEvent -> {
            ViewFactory.getInstance().setViewType(ViewType.PROFILE);
            homePane.setCenter(ViewFactory.getInstance().getUserPage(user));
        });
        settingsItem.setOnAction(actionEvent -> ViewFactory.getInstance().showSettingsWindow());
        logoutItem.setOnAction(actionEvent -> {
            ViewFactory.getInstance().closeSession();
            ViewFactory.getInstance().showAuthWindow();
        });
    }


    private void searchUser() {
        if (fldSearch.getText().trim().isBlank()) {
            fldSearch.setPromptText("FIELD IS EMPTY");
        }
        else {
            removeSelectedView();
            homePane.setCenter(ViewFactory.getInstance().getSearchedUsers(fldSearch.getText().trim()));
        }
    }

    private void getFollowedUsersTask(String username){
        ObservableList<User> followedUsers = user.getFollowedUsers();

        Task<List<User>> followedUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                return new UserDAOImpl().getFollowedUsers(username);
            }
        };

        new Thread(followedUsersTask).start();


        followedUsersTask.setOnSucceeded(workerStateEvent -> {
            for (User u : followedUsersTask.getValue()){
                HBox postBox = ViewFactory.getInstance().getFollowedUserPreview(u);
                followedUserList.getChildren().add(postBox);
            }
            followedUsers.addAll(followedUsersTask.getValue());

            followedUsers.addListener((ListChangeListener<? super User>) change -> {
                change.next();

                if(change.wasAdded()){
                    if(followedUsers.get(0) != null){
                        HBox postBox = ViewFactory.getInstance().getFollowedUserPreview(followedUsers.get(0));
                        followedUserList.getChildren().add(0, postBox);
                    }
                }
                else
                    followedUserList.getChildren().remove(change.getFrom());
            });
        });
    }

    @FXML
    private void setSelectedView(MouseEvent event){
        HBox box = (HBox) event.getSource();
        switch (box.getId()){
            case "hbFeed" -> homePane.setCenter(ViewFactory.getInstance().getFeedView());
            case "hbBrowse" -> homePane.setCenter(ViewFactory.getInstance().getBrowseView());
            case "hbTopPlaces" -> homePane.setCenter(ViewFactory.getInstance().getTopPlaces());
            case "hbCategories" -> homePane.setCenter(ViewFactory.getInstance().getCategories());
            case "hbCollections" -> homePane.setCenter(ViewFactory.getInstance().getCollectionView());
            case "hbYourGallery" -> homePane.setCenter(ViewFactory.getInstance().getYourGalleryView());
            case "hbFavorites" -> homePane.setCenter(ViewFactory.getInstance().getFavoritesView());
            case "hbTagged" -> homePane.setCenter(ViewFactory.getInstance().getTaggedView());
            case "hbYourCollections" -> homePane.setCenter(ViewFactory.getInstance().getYourCollectionView());
            case "hbUsedCollections" -> homePane.setCenter(ViewFactory.getInstance().getUsedCollectionView());
        }
        removeSelectedView();
        box.getStyleClass().add(0, "selected");
    }

    private void removeSelectedView(){
        if(homePane.lookup(".selected") != null)
            homePane.lookup(".selected").getStyleClass().remove(0);
    }

    public BorderPane getBorderPane() {return homePane;}
}
