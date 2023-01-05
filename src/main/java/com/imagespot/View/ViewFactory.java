package com.imagespot.View;

import com.imagespot.controller.*;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewFactory {

    private static ViewFactory viewFactory;
    private VBox browseView;
    private VBox yourGallery;
    private VBox feedView;

    private Parent homeRoot;
    private static User user;

    private HashMap<Integer, HBox> openedImages;

    public User getUser() {
        if(user == null) user = new User();
        return user;
    }

    public static synchronized ViewFactory getInstance() {

        if(viewFactory == null) {
            viewFactory = new ViewFactory();
        }
        return viewFactory;
    }

    //TODO: add fxml panes and getter

    public Parent getHomeRoot() {
        return homeRoot;
    }

    public VBox getFeedView() {

        if (feedView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new FeedController());
                feedView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return feedView;
    }
    public VBox getBrowseView() {

        if (browseView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new BrowseController());
                browseView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return browseView;
    }

    public VBox getYourGalleryView() {

        if(yourGallery == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new YourPhotoController());
                yourGallery = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yourGallery;
    }

    public VBox getPostPreview(Post post) {

        VBox postPreview = null;
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

    public HBox getUserPreview(User user) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/user-preview-view.fxml"));
        HBox userPreview;
        try {
            userPreview = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UserPreviewController controller = loader.getController();
        controller.init(user);
        return userPreview;
    }

    public VBox getSearchedUsers(String string) {

        VBox searchedUsers;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/searched-users-view.fxml"));
            searchedUsers = loader.load();
            SearchedUsersController controller = loader.getController();
            controller.init(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return searchedUsers;
    }

    public ScrollPane getUserPage(String username) {

        ScrollPane userPage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/user-page-view.fxml"));
            userPage = loader.load();
            UserPageController controller = loader.getController();
            controller.init(username);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return userPage;
    }

    public HBox getPostView(int idpost) {
        HBox postRoot = null;

        if(openedImages == null){
            openedImages = new HashMap<>();
        }

        if(openedImages.get(idpost) != null){
            return openedImages.get(idpost);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/post-view.fxml"));


            postRoot = loader.load();
            openedImages.put(idpost, postRoot);

            PostController controller = loader.getController();
            controller.init(idpost);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postRoot;
    }

    public void closeSession() {

        Stage stage = (Stage)homeRoot.getScene().getWindow();
        stage.close();
        viewFactory = null;
    }

    public void showAuthWindow() {

        user = null; //TODO: maybe not the best to reset the user here
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
        if(homeRoot == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-view.fxml"));
                homeRoot = loader.load();
                Scene scene = new Scene(homeRoot, 1050, 550);
                Stage stage = new Stage();
                stage.setMinHeight(400);
                stage.setMinWidth(400);
                stage.setTitle("Imagespot - Home");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        }
    }

    public void showSettingsWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/settings-view.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 560);
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create Window.", e);
        }

    }

    public void showAddInfoWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-info-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 483);
            Stage stage = new Stage();
            stage.setTitle("Add some info!");
            stage.initOwner(homeRoot.getScene().getWindow());

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
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setTitle("Login");
            Scene scene = new Scene(root, 602, 602);
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) { e.printStackTrace(); }
    }

}
