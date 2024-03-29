package com.imagespot.View;

import com.imagespot.Controller.*;
import com.imagespot.Controller.center.*;
import com.imagespot.Controller.center.categories.CategoriesController;
import com.imagespot.Controller.center.categories.CategoryController;
import com.imagespot.Controller.center.collections.CollectionPostsController;
import com.imagespot.Controller.center.collections.RecentCollectionsController;
import com.imagespot.Controller.center.collections.UsedCollectionsController;
import com.imagespot.Controller.center.collections.YourCollectionsController;
import com.imagespot.Model.Collection;
import com.imagespot.Model.Post;
import com.imagespot.Model.Subject;
import com.imagespot.Model.User;
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
    private VBox favoritesView;
    private VBox taggedView;
    private VBox topPlaces;
    private VBox categories;
    private VBox collections;
    private VBox yourCollections;
    private VBox usedCollections;
    private Parent homeRoot;
    private HomeController hm;
    private static User user;
    private HashMap<Integer, HBox> openedImages;
    private ViewType viewType;

    public void setViewType(ViewType type) {
        viewType = type;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public User getUser() {
        if (user == null) user = new User();
        return user;
    }

    public static synchronized ViewFactory getInstance() {
        if (viewFactory == null)
            viewFactory = new ViewFactory();

        return viewFactory;
    }


    public Parent getHomeRoot() {
        return homeRoot;
    }

    public HomeController getHomeController() {
        return hm;
    }

    public VBox getFeedView() {
        setViewType(ViewType.FEED);
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

    public VBox getTaggedView() {
        setViewType(ViewType.TAGGED);
        if (taggedView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new TaggedController());
                taggedView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taggedView;
    }

    public VBox getFavoritesView() {
        setViewType(ViewType.FAVOURITES);
        if (favoritesView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new FavouritesController());
                favoritesView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return favoritesView;
    }

    public VBox getPostsByPlace(String location, String type) {
        setViewType(ViewType.LOCATION);
        VBox postsByPlaceView = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
            loader.setController(new LocationController(location, type));
            postsByPlaceView = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postsByPlaceView;
    }

    public VBox getBrowseView() {
        setViewType(ViewType.EXPLORE);
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

    public VBox getCategories() {
        if(categories == null) {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
            loader.setController(new CategoriesController());
                categories = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return categories;
    }

    public VBox getPostsByCategory(String category) {
        setViewType(ViewType.CATEGORY);
        VBox postsByCategoryView = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
            loader.setController(new CategoryController(category));
            postsByCategoryView = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postsByCategoryView;
    }

    public VBox getPostsBySubject(Subject subject) {
        setViewType(ViewType.SUBJECT);
        VBox postsBySubjectView = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
            loader.setController(new SubjectController(subject));
            postsBySubjectView = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postsBySubjectView;
    }

    public VBox getTopPlaces() {
        setViewType(ViewType.TOP_PLACES);
        if (topPlaces == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/top-places-view.fxml"));
                topPlaces = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return topPlaces;
    }


    public VBox getYourGalleryView() {
        setViewType(ViewType.YOUR_GALLERY);
        if (yourGallery == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new YourGalleryController());
                yourGallery = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yourGallery;
    }

    public VBox getCollectionView() {
        viewType = ViewType.COLLECTIONS;
        if(collections == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new RecentCollectionsController());
                collections = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return collections;
    }

    public VBox getYourCollectionView(){
        viewType = ViewType.YOUR_COLLECTIONS;
        if(yourCollections == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new YourCollectionsController());
                yourCollections = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yourCollections;
    }

    public VBox getUsedCollectionView(){
        viewType = ViewType.USED_COLLECTIONS;
        if(usedCollections == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-center-view.fxml"));
                loader.setController(new UsedCollectionsController());
                usedCollections = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return usedCollections;
    }

    public VBox getPostPreview(Post post, ViewType type) {
        VBox postPreview = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/images-preview.fxml"));
            postPreview = loader.load();
            PreviewController controller = loader.getController();
            controller.setData(post, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postPreview;
    }

    public VBox getCollectionPreview(Collection collection, ViewType type){
        VBox collectionPreview = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/collection-preview-view.fxml"));
            collectionPreview = loader.load();
            CollectionPreviewController controller = loader.getController();
            controller.setData(collection, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return collectionPreview;
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

    public HBox getFollowedUserPreview(User user) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/followed-user-preview-view.fxml"));
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

    public ScrollPane getUserPage(User user) {
        ScrollPane userPage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/user-page-view.fxml"));
            userPage = loader.load();
            UserPageController controller = loader.getController();
            controller.init(user);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return userPage;
    }

    public HBox getPostView(int idpost) {
        HBox postRoot = null;

        if (openedImages == null)
            openedImages = new HashMap<>();

        if (openedImages.get(idpost) != null)
            return openedImages.get(idpost);

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

    public ScrollPane getCollectionPostsView(Collection collection) {
        ScrollPane collectionPostsPage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/collection-posts-view.fxml"));
            collectionPostsPage = loader.load();
            CollectionPostsController controller = loader.getController();
            controller.init(collection);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return collectionPostsPage;
    }

    public void closeSession() {
        Stage stage = (Stage) homeRoot.getScene().getWindow();
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
        if (homeRoot == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/home-view.fxml"));
                homeRoot = loader.load();
                hm = loader.getController();
                Scene scene = new Scene(homeRoot, 1050, 550);
                Stage stage = new Stage();
                stage.setMinHeight(400);
                stage.setMinWidth(400);
                stage.setTitle("Imagespot");
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
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setResizable(false);
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create Window.", e);
        }

    }

    public void showEditPostWindow(Post post) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/edit-post-view.fxml"));
        try {
            Parent root = loader.load();
            EditPostController controller = loader.getController();
            controller.init(post);
            Scene scene = new Scene(root, 400, 600);
            Stage stage = new Stage();
            stage.setTitle("Edit Post");
            stage.setResizable(false);
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create Window.", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAddInfoWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-info-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-post-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setTitle("Add Photo");
            Scene scene = new Scene(root, 602, 602);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddCollectionWindow(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-collection-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setTitle("Add Collection");
            Scene scene = new Scene(root, 602, 520);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) { e.printStackTrace(); }
    }

    public void showEditCollectionWindow(Collection collection){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/edit-collection-view.fxml"));
        try {
            Parent root = loader.load();
            EditCollectionController controller = loader.getController();
            controller.init(collection);
            Scene scene = new Scene(root, 400, 400);
            Stage stage = new Stage();
            stage.setTitle("Edit Collection");
            stage.setResizable(false);
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create Window.", e);
        }
    }

    public void showAddPostsToCollectionWindow(Collection collection){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/imagespot/add-posts-to-collection-view.fxml"));
        try {
            Parent root = loader.load();
            AddPostsToCollectionController controller = loader.getController();
            controller.init(collection);
            Scene scene = new Scene(root, 600, 768);
            Stage stage = new Stage();
            stage.setTitle("Add posts to " + collection.getName() + " collection");
            stage.setResizable(false);
            stage.initOwner(homeRoot.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create Window.", e);
        }
    }
}