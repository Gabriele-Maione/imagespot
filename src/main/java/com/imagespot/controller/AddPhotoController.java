package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.IdentityHashMap;

import org.apache.commons.io.FilenameUtils;


public class AddPhotoController {

    @FXML
    private Button add;

    @FXML
    private Button choose;
    @FXML
    private ImageView img;

    private File file;
    private PostDAOImpl postDAO;

    public AddPhotoController() throws SQLException {
        postDAO = new PostDAOImpl();
    }
    @FXML
    public void onBtnLoadImageClick(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

        file = fc.showOpenDialog(img.getScene().getWindow());
        img.setImage(new Image((file.getAbsolutePath())));
    }

    public void onBtnAddImageClick(ActionEvent event) throws SQLException, FileNotFoundException {

        Image i = new Image(file.getAbsolutePath());
        String res;
        String ext;
        int size;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        res = ((int)i.getHeight() + "x" + (int)i.getWidth());
        ext = FilenameUtils.getExtension(file.getName());
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        size = (int)(file.length()/1024);

        postDAO.addPost(file, res, null, size, ext, timestamp, "public", null, null, null);
    }


}
