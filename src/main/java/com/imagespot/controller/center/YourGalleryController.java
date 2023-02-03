package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class YourGalleryController extends CenterPaneController {
    private ObjectProperty<Boolean> isLoading;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isLoading = new SimpleObjectProperty<>();
        super.initialize(url, resourceBundle);
        name.setText("Your Gallery");
        addScrollPaneListener();
        addPostsRemovedListener();
        addPostAddedListener();
    }

    @Override
    protected void btnUpdateOnAction(){
        ViewFactory.getInstance().getUser().getPosts().clear();
        super.btnUpdateOnAction();
    }

    protected void loadPosts() {
        final Task<List<Post>> getUserPosts = new Task<>() {

            @Override
            protected ArrayList<Post> call() throws Exception {
                System.out.println();
                ArrayList<Post> posts = new PostDAOImpl().getUserPosts(ViewFactory.getInstance().getUser().getUsername(), (flowPane.getChildren().size() - offset) + offset);
                offset += posts.size();
                ViewFactory.getInstance().getUser().getPosts().addAll(posts);
                return posts;
            }
        };
        progressIndicator.visibleProperty().bind(getUserPosts.runningProperty());
        btnUpdate.disableProperty().bind(getUserPosts.runningProperty());
        isLoading.bind(getUserPosts.runningProperty());
        retrievePostsTask(getUserPosts);
    }

    private void addPostAddedListener(){
        ViewFactory.getInstance().getUser().getPosts().addListener((ListChangeListener<Post>) change -> {
            change.next();

            if(change.wasAdded() && !isLoading.getValue()){
                Post postAdded = change.getAddedSubList().get(0);
                VBox postBox = ViewFactory.getInstance().getPostPreview(postAdded);
                postBox.setId(String.valueOf(postAdded.getIdImage()));
                flowPane.getChildren().add(0, postBox);
                setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());
            }
        });
    }

}
