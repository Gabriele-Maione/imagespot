package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class YourPhotoController implements Initializable {
    @FXML
    private FlowPane flowPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            displayYourPhoto();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void displayYourPhoto() throws SQLException {

        List<Post> recentPosts = new PostDAOImpl().getUsersPost(ViewFactory.getInstance().getUser().getUsername());

        for (Post recentPost : recentPosts) {

            VBox postBox = ViewFactory.getInstance().getPostPreview(recentPost);

            flowPane.getChildren().add(postBox);

        }
    }
}
