package com.imagespot.controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

import static com.imagespot.Utils.TimeUtil.toDuration;
import static com.imagespot.Utils.Utils.setAvatarRounde;


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
    @FXML
    private Button modify;

    private Post post;


    public void setData(Post pst) {
        this.post = pst;
        image_preview.setImage((post.getPreview()));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        if (post.getProfile().getAvatar() != null) {
            avatar.setImage((post.getProfile().getAvatar()));
            setAvatarRounde(avatar);
        }

        passedTime.setText(toDuration(System.currentTimeMillis()-post.getDate().getTime()));
        setModify();
        addListeners();
    }

    private void setModify() {
        if(ViewFactory.getInstance().getViewType() == ViewType.YOUR_GALLERY){
            modify.setDisable(false);
            modify.setVisible(true);
        }
    }

    @FXML
    private void ModifyBtnOnAction() {
        ViewFactory.getInstance().showEditPostWindow(post);
    }

    public void addListeners() {
        avatar.setOnMouseClicked(event -> {
            ViewFactory.getInstance().setViewType(ViewType.PROFILE);
            BorderPane borderPane = (BorderPane) image_preview.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getUserPage(post.getProfile()));
        });
    }

    @FXML
    public void previewOnClick() throws IOException {
        username.getScene().setRoot(ViewFactory.getInstance().getPostView(post.getIdImage()));
    }


}
