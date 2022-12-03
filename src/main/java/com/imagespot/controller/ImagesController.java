package com.imagespot.controller;

import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ImagesController {

    @FXML
    private Label date;

    @FXML
    private ImageView image_preview;

    @FXML
    private Label usrname;


    public void setData(Post post) {

        //TODO: add all images data
        Image image = new Image(getClass().getResourceAsStream((post.getPhotoname())));
        image_preview.setImage(image);
    }
}
