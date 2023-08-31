package com.imagespot.Controller.center;

import com.imagespot.ImplementationPostgresDAO.BookmarkDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.Post;
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
        addScrollPaneListener();
        addPostsRemovedListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getFavourites = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new BookmarkDAOImpl().getUserBookmarks(offset);
                offset += posts.size();
                ViewFactory.getInstance().getUser().setBookmarks(posts);
                return posts;
            }
        };
        retrievePostsTask(getFavourites);
        progressIndicator.visibleProperty().bind(getFavourites.runningProperty());
        btnUpdate.disableProperty().bind(getFavourites.runningProperty());
    }

}
