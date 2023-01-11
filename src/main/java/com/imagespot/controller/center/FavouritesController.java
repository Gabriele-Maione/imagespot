package com.imagespot.controller.center;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FavouritesController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);
        name.setText("Favourites");
        scrollPane.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
            if (scrollPosition.intValue() == 1 && flowPane.getChildren().size() % 20 == 0) { //scrollPosition == 1 -> scroll have reached the bottom
                loadPosts();
            }
        });
    }

    protected void loadPosts() {
        final Task<List<Post>> getFavourites = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                new BookmarkDAOImpl().getUserBookmarks();
                return ViewFactory.getInstance().getUser().getBookmarks();
            }
        };
        System.out.println("FAVOURITES CONTROLLER NAPULLL");
        retrievePostsTask(getFavourites);
        progressIndicator.visibleProperty().bind(getFavourites.runningProperty());
    }

}
