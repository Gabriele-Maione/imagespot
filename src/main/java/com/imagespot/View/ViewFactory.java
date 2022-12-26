package com.imagespot.View;

import com.imagespot.controller.*;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewFactory {

    private static ViewFactory viewFactory;
    private VBox browseView;
    private VBox yourGallery;
    private VBox postPreview;
    private final StringProperty clientSelectedMenuItem;

    private static User user;

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
    }

    public User getUser() {
        if(user == null) user = new User();
        return user;
    }

    public StringProperty getClientSelectedMenuItem() {
        return clientSelectedMenuItem;
    }

    public static synchronized ViewFactory getInstance() {

        if(viewFactory == null) {
            viewFactory = new ViewFactory();
        }
        return viewFactory;
    }

    //TODO: add fxml panes and getter
    public VBox getBrowseView() {

        if(browseView == null) {
            try {
                browseView = new FXMLLoader(getClass().getResource("/com/imagespot/browse-view.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return browseView;
    }

    public VBox getYourGalleryView() {

        if(yourGallery == null) {
            try {
                yourGallery = new FXMLLoader(getClass().getResource("/com/imagespot/your-gallery-view.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yourGallery;
    }

    public VBox getPostPreview(Post post) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/images-preview.fxml"));
            postPreview = loader.load();
            ImagesController controller = loader.getController();
            controller.setData(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postPreview;
    }


    public void showAuthWindow() {
        Scene scene;
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/imagespot/sign-in-view.fxml"));

            stage.setTitle("Login");
            scene = new Scene(fxmlLoader.load(), 480, 600);
            stage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showHomeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 550);
            Stage stage = new Stage();
            stage.setTitle("Imagespot - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    public void showAddInfoWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-info-view.fxml"));
            Parent root = (Parent)loader.load();
            Scene scene = new Scene(root, 600, 483);
            Stage stage = new Stage();
            stage.setTitle("Add some info!");
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    public void showAddPhotoWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-photo-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            Scene scene = new Scene(root, 602, 602);
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) { e.printStackTrace(); }
    }

}
