package com.imagespot.Controller.center;

import com.imagespot.ImplementationPostgresDAO.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FeedController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Home");
        addScrollPaneListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getFeed = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername(), offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getFeed);
        progressIndicator.visibleProperty().bind(getFeed.runningProperty());
        btnUpdate.disableProperty().bind(getFeed.runningProperty());
    }
}
