package com.imagespot.controller.center;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
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
    }

    protected void loadPosts() {
        final Task<List<Post>> getFavourites = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new BookmarkDAOImpl().getUserBookmarks();
                ViewFactory.getInstance().getUser().setBookmarks(posts);
                return posts;
            }
        };
        retrievePostsTask(getFavourites, true);
        progressIndicator.visibleProperty().bind(getFavourites.runningProperty());
    }

}
