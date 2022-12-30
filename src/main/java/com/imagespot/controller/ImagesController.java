package com.imagespot.controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.util.Locale;

import static com.imagespot.Utils.Utils.crop;


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
        image_preview.setImage(crop(new Image(post.getPreview())));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        avatar.setImage(new Image(post.getProfile().getAvatar()));
        passedTime.setText(new PrettyTime(Locale.forLanguageTag("en")).format(post.getDate()));
    }

    @FXML
    public void previewOnClick() {

        System.out.println(post.getProfile().getAvatar());
        ViewFactory.getInstance().showPostView(post);
    }

}
