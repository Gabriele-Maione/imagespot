package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class YourGalleryController extends CenterPaneController {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Your Gallery");
    }

    protected void loadPosts() {
        final Task<List<Post>> getUserPosts = new Task<>() {
            ArrayList<Post> posts;
            @Override
            protected ArrayList<Post> call() throws Exception {
                posts = new PostDAOImpl().getUserPosts(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                if(posts != null)
                    lastPostDate = posts.get(posts.size() - 1).getDate();
                ViewFactory.getInstance().getUser().getPosts().addAll(posts);
                return posts;
            }
        };
        System.out.println("YOUR GALLERY CONTROLLER BABYYY");
        retrievePostsTask(getUserPosts);
        progressIndicator.visibleProperty().bind(getUserPosts.runningProperty());
    }

}
