package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class UserPageController {

    @FXML
    private ImageView avatar;

    @FXML
    private Label bio;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Label follower;

    @FXML
    private Label following;

    @FXML
    private Label name;

    @FXML
    private Label post;

    @FXML
    private Label username;

    private User user;
    public void init(String username) throws SQLException {
        user = new UserDAOImpl().getUserInfoForPreview(username);

        if (user.getAvatar() != null)
            avatar.setImage(new Image(user.getAvatar()));
        name.setText(user.getName());
        this.username.setText("@" + user.getUsername());
        if (user.getBio() != null)
            bio.setText(user.getBio());
        displayUserPosts();
    }

    protected void displayUserPosts() throws SQLException {

        List<Post> userPosts = new PostDAOImpl().getUsersPost(user.getUsername());

        for (Post userPost : userPosts) {

            VBox postBox = ViewFactory.getInstance().getPostPreview(userPost);
            flowPane.getChildren().add(postBox);

        }
    }
}
