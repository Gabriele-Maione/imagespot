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
import javafx.scene.layout.FlowPane;
import java.net.URL;
import java.sql.SQLException;
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

    private final ViewType type;

    public CenterPaneController() {
        this.type = ViewFactory.getInstance().getViewType();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        try {
            setChildren();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    public void loadPosts() {
        final Task<List<Post>> getPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                switch (type) {
                    case EXPLORE -> {
                        name.setText("Explore");
                        return new PostDAOImpl().getRecentPost();
                    }
                    case YOUR_GALLERY -> {
                        name.setText("Your Gallery");
                        return new PostDAOImpl().getUsersPost(ViewFactory.getInstance().getUser().getUsername());
                    }
                    case FEED -> {
                        name.setText("Home");
                        return new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername());
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
