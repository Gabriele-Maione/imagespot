package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.sql.Timestamp;
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
    private ToggleButton followButton;

    @FXML
    private Label username;

    private User user;
    public void init(String username) throws SQLException {

        getUserInfoTask(username);

        //TODO: DO IT WITH TASKS!!!!
        post.setText("Post: " + new UserDAOImpl().userPostsCount(username));
        follower.setText("Follower: " + new UserDAOImpl().userFollowerCount(username));
        following.setText("Following: " + new UserDAOImpl().userFollowingCount(username));
    }

    @FXML
    private void followButtonOnAction() {
        followUnfollowTask(followButton.isSelected());
    }

    public void followUnfollowTask(boolean action) {
        final Task<Void> followingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(action) new UserDAOImpl().setFollow(user.getUsername());
                else new UserDAOImpl().removeFollow(user.getUsername());
                return null;
            }
        };
        new Thread(followingTask).start();
        followingTask.setOnSucceeded(workerStateEvent -> {
            if (followButton.isSelected()) followButton.setText("UNFOLLOW");
            else followButton.setText("FOLLOW");
        });
    }


    public void getUserInfoTask(String username) {
        final Task<User> userInfoTask= new Task<>() {

            @Override
            protected User call() throws SQLException {
                return new UserDAOImpl().getUserInfoForPreview(username);
            }
        };
        new Thread(userInfoTask).start();
        userInfoTask.setOnSucceeded(workerStateEvent -> {
            user = userInfoTask.getValue();
            if (user.getAvatar() != null) {
                avatar.setImage(user.getAvatar());
            }
            name.setText(user.getName());
            this.username.setText("@" + user.getUsername());
            if (user.getBio() != null)
                bio.setText(user.getBio());
            checkFollowingTask();
            initUserPosts();
        });
    }
    public void checkFollowingTask() {
        final Task<Boolean> checkFollowing = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return new UserDAOImpl().checkFollow(user.getUsername());
            }
        };
        new Thread(checkFollowing).start();
        checkFollowing.setOnSucceeded(workerStateEvent -> {
            if (checkFollowing.getValue()) {
                followButton.setSelected(true);
                followButton.setText("UNFOLLOW");
            }
            else {
                followButton.setSelected(false);
                followButton.setText("FOLLOW");
            }
        });
    }

    public void initUserPosts() {
        final Task<List<Post>> userPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                return new PostDAOImpl().getUsersPublicPost(user.getUsername());
            }
        };
        Utils.retrievePostsTask(userPostsTask, flowPane);
    }

}
