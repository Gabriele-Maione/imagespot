package com.imagespot.controller.center.categories;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.controller.center.CenterPaneController;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryController extends CenterPaneController {

    private String category;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText(category);
    }

    public CategoryController(String category) {
        this.category = category;
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getPostsByLocation = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getPostsByCategory(category, offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getPostsByLocation);
        progressIndicator.visibleProperty().bind(getPostsByLocation.runningProperty());
        btnUpdate.disableProperty().bind(getPostsByLocation.runningProperty());
    }
}
