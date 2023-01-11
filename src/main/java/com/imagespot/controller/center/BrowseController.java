package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BrowseController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Recently Added");
    }

    protected void loadPosts() {
        final Task<List<Post>> getRecentPosts = new Task<>() {
            ArrayList<Post> posts;
            @Override
            protected ArrayList<Post> call() throws Exception {
                posts = new PostDAOImpl().getRecentPosts(lastPostDate);
                if(posts != null)
                    lastPostDate = posts.get(posts.size() - 1).getDate();
                return posts;
            }
        };
        System.out.println("BROWSE CONTROLLER WEEE");
        retrievePostsTask(getRecentPosts);
        progressIndicator.visibleProperty().bind(getRecentPosts.runningProperty());
    }
}
