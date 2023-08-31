package com.imagespot.Controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Model.Post;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
        btnUpdate.setOnAction(actionEvent -> {
            flowPane.getChildren().clear();
            offset = 0;
            ViewFactory.getInstance().getUser().getPosts().clear();
            loadPosts();
        });
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> getUserPosts = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
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
                VBox postBox = ViewFactory.getInstance().getPostPreview(postAdded, ViewType.YOUR_GALLERY);
                postBox.setId(String.valueOf(postAdded.getIdImage()));

                if(flowPane.getChildren().size() == 1 && flowPane.getChildren().get(0) instanceof Label){
                    flowPane.getChildren().clear();
                    flowPane.setAlignment(Pos.TOP_LEFT);
                }

                flowPane.getChildren().add(0, postBox);
                setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());
            }
        });
    }

}
