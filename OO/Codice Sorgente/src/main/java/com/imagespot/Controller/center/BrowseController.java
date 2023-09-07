package com.imagespot.Controller.center;

import com.imagespot.ImplementationPostgresDAO.PostDAOImpl;
import com.imagespot.Model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BrowseController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Recently Added");
        addScrollPaneListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getRecentPosts = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getRecentPosts(offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getRecentPosts);
        progressIndicator.visibleProperty().bind(getRecentPosts.runningProperty());
        btnUpdate.disableProperty().bind(getRecentPosts.runningProperty());
    }
}
