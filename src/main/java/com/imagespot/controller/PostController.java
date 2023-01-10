package com.imagespot.controller;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import static com.imagespot.Utils.Utils.getMostCommonColour;

public class PostController {

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
    @FXML
    private Button btnDownload;
    @FXML
    private ToggleButton likeBtn;
    @FXML
    private Label deviceLbl;
    @FXML
    private Label date;
    private Post post;

    private Image image;

    public void init(int idpost) throws SQLException {

        //imgContainer is the pane that contains the image
        //photo is my imageview
        btnDownload.setDisable(true);
        likeBtn.setDisable(true);
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
                avatar.setImage((post.getProfile().getAvatar()));
            }
            date.setText(new SimpleDateFormat("h:mm a '·' d MMM yyyy").format(post.getDate()));
            description.setText(post.getDescription());
            deviceLbl.setText(post.getDevice().getBrand() + " " + post.getDevice().getModel());
            likeBtn.setText(String.valueOf(post.getLikes().size()));

            initLikeBtn();
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
            btnDownload.setDisable(false);
            image = photoTask.getValue();
            post.setIdImage(idpost);

            setPostBackgroundColor();
        });

    }

    private void initLikeBtn() {
        Task<Boolean> isLikedTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return new BookmarkDAOImpl().isLiked(post.getIdImage());
            }
        };
        new Thread(isLikedTask).start();
        isLikedTask.setOnSucceeded(workerStateEvent -> {
            likeBtn.setDisable(false);
            if(isLikedTask.getValue())
                likeBtn.setSelected(true);
            else likeBtn.setSelected(false);
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

            imgContainer.setStyle("-fx-background-color: rgb(" + postBackgroundTask.getValue() +")");
            photo.setImage(image);
        });
    }

    @FXML
    private void likeBtnOnAction() {

        likeAction();
    }

    private void likeAction() {
        final Task<Void> setLike = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(likeBtn.isSelected()) {
                    post.getLikes().add(ViewFactory.getInstance().getUser());
                    new BookmarkDAOImpl().addBookmark(post.getIdImage());
                }
                else
                    for(int i = 0; i < post.getLikes().size(); i++) {
                        if(ViewFactory.getInstance().getUser().getUsername()
                                .equals(post.getLikes().get(i).getUsername()))
                            post.getLikes().remove(i);
                        new BookmarkDAOImpl().removeBookmark(post.getIdImage());
                    }
                return null;
            }
        };
        new Thread(setLike).start();
        setLike.setOnSucceeded(workerStateEvent -> {
            likeBtn.setText(String.valueOf(post.getLikes().size())); //TODO: do it with listeners
        });
    }

    @FXML
    private void downloadBtnOnAction() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*." + post.getExtension()));
        fileChooser.setInitialFileName("photo_" + post.getIdImage());

        File file = fileChooser.showSaveDialog(username.getScene().getWindow());
        if(file != null) {
            btnDownload.setDisable(true);
            retrievePhotoFileTask(file);
        }
    }

    private void retrievePhotoFileTask(File file) {
        final Task<Void> fileTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Downloading...");
                InputStream inputStream = new PostDAOImpl().getPhotoFile(post.getIdImage());
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    int byteRead = -1;

                    while ((byteRead = inputStream.read()) != -1) {
                        outputStream.write(byteRead);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                outputStream.close();
                inputStream.close();
                return null;
            }
        };
        btnDownload.textProperty().bind(fileTask.messageProperty());
        new Thread(fileTask).start();
        fileTask.setOnSucceeded(workerStateEvent -> {
            btnDownload.textProperty().unbind();
            btnDownload.setText("Done!");
        });
    }

    @FXML
    public void buttonCloseOnAction() {

        username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
    }
}
