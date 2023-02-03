package com.imagespot.controller.center;

import com.imagespot.DAOImpl.TaggedUserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TaggedController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Tagged");
        addScrollPaneListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getTagged = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> posts = new TaggedUserDAOImpl().getTag(ViewFactory.getInstance().getUser().getUsername(), offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getTagged);
        progressIndicator.visibleProperty().bind(getTagged.runningProperty());
        btnUpdate.disableProperty().bind(getTagged.runningProperty());
    }
}
