package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

        List<Post> recentPosts = new ArrayList<>(new PostDAOImpl().getUsersPost(ViewFactory.getInstance().getUser().getUsername()));

        for (int i = 0; i < recentPosts.size(); i++) {

            VBox postBox = ViewFactory.getInstance().getPostPreview(recentPosts.get(i));

            flowPane.getChildren().add(postBox);
            GridPane.setMargin(postBox, new Insets(10));

        }
    }
}
