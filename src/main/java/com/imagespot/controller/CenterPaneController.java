package com.imagespot.controller;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CenterPaneController implements Initializable {
    @FXML
    private FlowPane flowPane;
    @FXML
    private Label name;
    @FXML
    private Button btnUpdate;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ScrollPane scrollPane;
    private final ViewType type;
    private Timestamp lastPostDate;


    public CenterPaneController() {
        this.type = ViewFactory.getInstance().getViewType();
        lastPostDate = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        try {
            setChildren();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(type != ViewType.FAVOURITES){ //TODO temporaneamente disabilitao per il ViewType FAVOURITES
            scrollPane.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
                if(scrollPosition.intValue() == 1 && flowPane.getChildren().size() % 20 == 0){ //scrollPosition == 1 -> scroll have reached the bottom
                    loadPosts();
                }
            });
        }

    }

    private void btnUpdateOnAction() {
        btnUpdate.setOnAction(actionEvent -> {
            try {
                flowPane.getChildren().clear();
                setChildren();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    protected void setChildren() throws SQLException {
        loadPosts();
    }

    private void loadPosts() {
        final Task<List<Post>> getPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts;

                switch (type) {
                    case EXPLORE -> {
                        name.setText("Explore");
                        posts = new PostDAOImpl().getRecentPosts(lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        return posts;
                    }
                    case YOUR_GALLERY -> {
                        name.setText("Your Gallery");
                        posts = new PostDAOImpl().getUserPosts(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        ViewFactory.getInstance().getUser().getPosts().addAll(posts);
                        return posts;
                    }
                    case FEED -> {
                        name.setText("Home");
                        posts = new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        return posts;
                    }
                    case FAVOURITES -> {
                        name.setText("Favourites");
                        new BookmarkDAOImpl().getUserBookmarks();
                        return ViewFactory.getInstance().getUser().getBookmarks();
                    }
                    default -> {
                        return null;
                    }
                }
            }
        };

        Utils.retrievePostsTask(getPostsTask, flowPane);
        progressIndicator.visibleProperty().bind(getPostsTask.runningProperty());
    }
}
