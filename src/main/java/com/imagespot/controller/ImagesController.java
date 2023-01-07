package com.imagespot.controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.imagespot.Utils.TimeUtil.toDuration;


public class ImagesController {

    @FXML
    private Label date;
    @FXML
    private ImageView image_preview;
    @FXML
    private Label username;
    @FXML
    private Label name;
    @FXML
    private ImageView avatar;
    @FXML
    private Label passedTime;
    @FXML
    private VBox preview;

    private Post post;


    public void setData(Post pst) {

        this.post = pst;
        image_preview.setImage((post.getPreview()));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        if (post.getProfile().getAvatar() != null) {
            avatar.setImage((post.getProfile().getAvatar()));
        }

        passedTime.setText(toDuration(System.currentTimeMillis()-post.getDate().getTime()));
        addListeners();
    }

    public void addListeners() {

        avatar.setOnMouseClicked(event -> {
            BorderPane borderPane = (BorderPane) image_preview.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getUserPage(post.getProfile().getUsername()));
        });
    }

    @FXML
    public void previewOnClick() throws IOException {

        username.getScene().setRoot(ViewFactory.getInstance().getPostView(post.getIdImage()));
    }


}
