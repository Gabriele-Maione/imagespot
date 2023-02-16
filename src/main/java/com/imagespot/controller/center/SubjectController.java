package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.model.Post;
import com.imagespot.model.Subject;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SubjectController extends CenterPaneController {

    private final Subject subject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText(subject.getSubject());
    }

    public SubjectController(Subject subject) {
        this.subject = subject;
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getSubjectPosts = new Task<>() {
            @Override
            protected ArrayList<Post> call() {
                ArrayList<Post> posts = new PostDAOImpl().getPostsBySubject(subject.getId(), offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(getSubjectPosts);
        progressIndicator.visibleProperty().bind(getSubjectPosts.runningProperty());
        btnUpdate.disableProperty().bind(getSubjectPosts.runningProperty());
    }
}
