package com.imagespot.controller;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.Subject;
import com.imagespot.model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.imagespot.Utils.Utils.getMostCommonColour;
import static com.imagespot.Utils.Utils.setAvatarRounde;

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
    @FXML
    private VBox categoryVBox;
    @FXML
    private VBox locationVBox;
    @FXML
    private VBox tagVBox;
    @FXML
    private Label locationLbl;
    private Post post;
    private Image image;
    HomeController hm;

    public void init(int idpost) throws SQLException {
        //imgContainer is the pane that contains the image
        //photo is my imageview
        hm = ViewFactory.getInstance().getHomeController();
        btnDownload.setDisable(true);
        likeBtn.setDisable(true);
        photo.fitWidthProperty().bind(root.widthProperty().subtract(postSidebar.widthProperty()));
        photo.fitHeightProperty().bind(root.heightProperty());

        locationVBox.setManaged(false);
        locationVBox.setVisible(false);

        tagVBox.setManaged(false);
        tagVBox.setVisible(false);

        categoryVBox.setManaged(false);
        categoryVBox.setVisible(false);

        invokeInit(idpost);
    }

    private void invokeInit(int idpost) {
        final Task<Post> postTask = new Task<>() {
            @Override
            protected Post call() {
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
                setAvatarRounde(avatar);
            }
            date.setText(new SimpleDateFormat("h:mm a '·' d MMM yyyy").format(post.getDate()));
            description.setText(post.getDescription());
            deviceLbl.setText(post.getDevice().getBrand() + " " + post.getDevice().getModel());
            likeBtn.setText(String.valueOf(post.getLikesNumber()));

            if (!post.getTaggedUsers().isEmpty()) {
                System.out.println(post.getTaggedUsers().isEmpty());

                for (User user : post.getTaggedUsers())
                    tagVBox.getChildren().add(getContainerForTag(user));
                tagVBox.setManaged(true);
                tagVBox.setVisible(true);
            }

            if (post.getLocation() != null) {
                locationLbl.setText(post.getLocation().getFormatted_address());
                locationLbl.setOnMouseClicked(mouseEvent -> {
                    hm.getBorderPane().setCenter(ViewFactory.getInstance().getPostsByPlace(post.getLocation().getFormatted_address(), "formatted_address"));
                    username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
                });
                locationVBox.setManaged(true);
                locationVBox.setVisible(true);
            }

            if (!post.getSubjects().isEmpty()) {
                for (Subject subject : post.getSubjects())
                    categoryVBox.getChildren().add(getContainerCategory(subject));
                categoryVBox.setManaged(true);
                categoryVBox.setVisible(true);
            }

            initLikeBtn();
        });
    }

    private void invokePhoto(int idpost) {
        final Task<Image> photoTask = new Task<>() {
            @Override
            protected Image call() {
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
        Task<Boolean> isLikedTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return new BookmarkDAOImpl().isLiked(post.getIdImage());
            }
        };
        new Thread(isLikedTask).start();
        isLikedTask.setOnSucceeded(workerStateEvent -> {
            likeBtn.setDisable(false);
            likeBtn.setSelected(isLikedTask.getValue());
        });
    }

    private void setPostBackgroundColor() {
        Task<String> postBackgroundTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return getMostCommonColour(image);
            }
        };

        new Thread(postBackgroundTask).start();
        postBackgroundTask.setOnSucceeded(workerStateEvent -> {
            imgContainer.setStyle("-fx-background-color: rgb(" + postBackgroundTask.getValue() + ")");
            photo.setImage(image);
        });
    }

    @FXML
    private void likeBtnOnAction() {
        likeAction();
    }

    private void likeAction() {
        final Task<Void> setLike = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (likeBtn.isSelected()) {
                    post.setLikesNumber(post.getLikesNumber() + 1);
                    new BookmarkDAOImpl().addBookmark(post.getIdImage());
                } else {
                    post.setLikesNumber(post.getLikesNumber() - 1);
                    new BookmarkDAOImpl().removeBookmark(post.getIdImage());
                }
                return null;
            }
        };
        new Thread(setLike).start();
        setLike.setOnSucceeded(workerStateEvent -> {
            likeBtn.setText(String.valueOf(post.getLikesNumber()));
        });
    }

    @FXML
    private void downloadBtnOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*." + post.getExtension()));
        fileChooser.setInitialFileName("photo_" + post.getIdImage());

        File file = fileChooser.showSaveDialog(username.getScene().getWindow());
        if (file != null) {
            btnDownload.setDisable(true);
            retrievePhotoFileTask(file);
        }
    }

    private void retrievePhotoFileTask(File file) {
        final Task<Void> fileTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Downloading...");
                InputStream inputStream = new PostDAOImpl().getPhotoFile(post.getIdImage());
                OutputStream outputStream;

                try {
                    outputStream = new FileOutputStream(file);
                    int byteRead;
                    byte[] buffer = new byte[1024];

                    while ((byteRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, byteRead);
                    }
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
            btnDownload.setText("Download again");
            btnDownload.setDisable(false);
        });
    }

    @FXML
    private void buttonCloseOnAction() {
        username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
    }
    @FXML
    private void userProfileOnClick() {
        hm.getBorderPane().setCenter(ViewFactory.getInstance().getUserPage(post.getProfile()));
        username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
    }

    private HBox getContainerForTag(User user) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("tagged");
        avatar = new ImageView();
        avatar.setFitHeight(25);
        avatar.setFitWidth(25);
        avatar.setPreserveRatio(true);
        avatar.setPickOnBounds(true);
        setAvatarRounde(avatar);
        if (user.getAvatar() != null)
            avatar.setImage(user.getAvatar());
        else
            avatar.setImage(new Image(getClass().getResourceAsStream("/icons/bear_icon.png")));

        name = new Label(user.getName());
        name.setFont(new Font("System Bold", 14));

        username = new Label("@" + user.getUsername());
        username.setTextFill(Paint.valueOf("#5b5b5b"));

        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPrefHeight(30);
        hBox.setPrefWidth(200);
        hBox.setSpacing(10);
        hBox.setOnMouseClicked(mouseEvent -> {
            hm.getBorderPane().setCenter(ViewFactory.getInstance().getUserPage(user));
            username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
        });

        hBox.getChildren().addAll(avatar, name, username);
        return hBox;
    }

    private VBox getContainerCategory(Subject subject) {
        VBox vBox = new VBox();
        Label categoryLabel = new Label(subject.getCategory());
        categoryLabel.getStyleClass().add("clickable");
        categoryLabel.setFont(new Font("System Bold", 14.0));
        categoryLabel.setOnMouseClicked(mouseEvent -> {
            hm.getBorderPane().setCenter(ViewFactory.getInstance().getPostsByCategory(subject.getCategory()));
            username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
        });
        Label subjectLabel = new Label(subject.getSubject());
        subjectLabel.setOnMouseClicked(mouseEvent -> {
            hm.getBorderPane().setCenter(ViewFactory.getInstance().getPostsBySubject(subject));
            username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
        });
        vBox.getChildren().addAll(categoryLabel, subjectLabel);
        return vBox;
    }
}
