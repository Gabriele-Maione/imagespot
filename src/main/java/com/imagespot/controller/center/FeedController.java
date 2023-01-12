package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FeedController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Feed");
        addScrollPaneListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getFeed = new Task<>() {
            ArrayList<Post> posts;
            @Override
            protected ArrayList<Post> call() throws Exception {
                posts = new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                if(posts != null)
                    lastPostDate = posts.get(posts.size() - 1).getDate();
                return posts;
            }
        };
        retrievePostsTask(getFeed, false);
        progressIndicator.visibleProperty().bind(getFeed.runningProperty());
    }
}
