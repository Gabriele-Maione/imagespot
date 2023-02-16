package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LocationController extends CenterPaneController {

    private String location;
    private String type;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText(location);
    }

    public LocationController(String location, String type) {
        this.location = location;
        this.type = type;
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getPostsByLocation = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getPostsByLocation(location, type, offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getPostsByLocation);
        progressIndicator.visibleProperty().bind(getPostsByLocation.runningProperty());
        btnUpdate.disableProperty().bind(getPostsByLocation.runningProperty());
    }
}
