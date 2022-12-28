package com.imagespot.controller;

import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import org.ocpsoft.prettytime.PrettyTime;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PostController implements Initializable {

    @FXML
    private ImageView avatar;

    @FXML
    private Button btnFollow;

    @FXML
    private TextArea fldDescription;

    @FXML
    private Label name;

    @FXML
    private ImageView photo;

    @FXML
    private Label username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fldDescription.setEditable(false);
    }

    public void init(Post post) {

        photo.setImage(new Image(post.getPreview()));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        avatar.setImage(new Image(post.getProfile().getAvatar()));
        fldDescription.setText(post.getDescription());
    }
}
