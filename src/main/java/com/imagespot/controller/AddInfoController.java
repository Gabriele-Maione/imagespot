package com.imagespot.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class AddInfoController {

    @FXML
    private Button btnAvatar;

    @FXML
    private Button btnSubmit;

    @FXML
    private ChoiceBox<?> cbGender;

    @FXML
    private Hyperlink hlinkSkip;

    @FXML
    private ImageView imgPreview;

    private File avatar;

    @FXML
    private void btnAvatarOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

        avatar = fc.showOpenDialog(imgPreview.getScene().getWindow());
        imgPreview.setImage(new Image((avatar.getAbsolutePath())));
    }

}

