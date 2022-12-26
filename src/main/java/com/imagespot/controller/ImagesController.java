package com.imagespot.controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.ocpsoft.prettytime.PrettyTime;


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

    public void setData(Post post) {

        image_preview.setImage(crop(new Image(post.getPhoto())));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        avatar.setImage(new Image(post.getProfile().getAvatar()));
        passedTime.setText(new PrettyTime(Locale.forLanguageTag("en")).format(post.getDate()));
    }

    private Image crop(Image img) {

        double d = Math.min(img.getWidth(),img.getHeight());
        double x = (d-img.getWidth())/2;
        double y = (d-img.getHeight())/2;

        Canvas canvas = new Canvas(d, d);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(img, x, y);

        return canvas.snapshot(null, null);
    }
}
