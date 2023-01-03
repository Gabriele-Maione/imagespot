package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FeedController implements Initializable {

    @FXML
    private FlowPane flowPane;
    @FXML
    private Label name;
    @FXML
    private Button btnUpdate;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        name.setText("Feed");

        btnUpdateOnAction();
        try {
            displayFeed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void btnUpdateOnAction() {
        btnUpdate.setOnAction(actionEvent -> {
            try {
                flowPane.getChildren().clear();
                displayFeed();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void displayFeed() throws SQLException {

        initFeedTask();
    }

    public void initFeedTask() {
        final Task<List<Post>> recentPostsPreview = new Task<List<Post>>() {
            @Override
            protected List<Post> call() throws Exception {
                return new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername());
            }
        };
        Utils.retrievePostsTask(recentPostsPreview, flowPane);
    }
}