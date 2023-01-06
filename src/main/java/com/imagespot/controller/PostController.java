package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.imagespot.Utils.Utils.crop;
import static com.imagespot.Utils.Utils.getMostCommonColour;

public class PostController implements Initializable {

    @FXML
    private HBox root;

    @FXML
    private VBox postSidebar;
    @FXML
    private ImageView avatar;

    @FXML
    private Button btnFollow;

    @FXML
    private Label description;

    @FXML
    private Label name;

    @FXML
    private ImageView photo;
    @FXML
    private StackPane imgContainer;
    @FXML
    private Label username;
    @FXML
    private Button btnClose;
    @FXML
    private ProgressIndicator loadingIndicator;
    private Post post;

    private Image image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void init(int idpost) throws SQLException {

        //imgContainer is the pane that contains the image
        //photo is my imageview
        photo.fitWidthProperty().bind(root.widthProperty().subtract(postSidebar.widthProperty()));
        photo.fitHeightProperty().bind(root.heightProperty());

        invokeInit(idpost);
    }

    private void invokeInit(int idpost) {
        final Task<Post> postTask = new Task<>() {
            @Override
            protected Post call() throws Exception {
                return new PostDAOImpl().getPost(idpost);
            }

        };

        new Thread(postTask).start();
        postTask.setOnSucceeded(workerStateEvent -> {

            invokePhoto(idpost);
            post = postTask.getValue();

            name.setText(post.getProfile().getName());
            username.setText("@" + post.getProfile().getUsername());
            if (post.getProfile().getAvatar() != null) {
                avatar.setImage(crop(post.getProfile().getAvatar()));
            }
            description.setText(post.getDescription());
        });
    }

    private void invokePhoto(int idpost) {
        final Task<Image> photoTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return new PostDAOImpl().getPhoto(idpost);
            }
        };

        loadingIndicator.visibleProperty().bind(photoTask.runningProperty());

        new Thread(photoTask).start();
        photoTask.setOnSucceeded(workerStateEvent -> {
            image = photoTask.getValue();
            setPostBackgroundColor();
        });

    }

    private void setPostBackgroundColor(){
        Task<String> postBackgroundTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return getMostCommonColour(image);
            }
        };

        new Thread(postBackgroundTask).start();
        postBackgroundTask.setOnSucceeded(workerStateEvent -> {
            photo.setImage(image);
            imgContainer.setStyle("-fx-background-color: rgb(" + postBackgroundTask.getValue() +")");
        });
    }



    @FXML
    public void buttonCloseOnAction() {

        username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
    }
}
