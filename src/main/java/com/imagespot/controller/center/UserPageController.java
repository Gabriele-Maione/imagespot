package com.imagespot.controller.center;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserPageController extends CenterPaneController {

    @FXML
    private ImageView avatar;
    @FXML
    private Label bio;

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
    @FXML
    private ProgressIndicator progressIndicator;
    private User user;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Chiamato solo per fare in modo che nn chiami quello della superclasse
    }

    public void init(User u) throws SQLException {
        this.user = u;
        lastPostDate = null;

        if(this.user != null){
            setUserInfo();
            getUserStatsTask();
            loadPosts();

            scrollPane.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
                if(scrollPosition.intValue() == 1 && flowPane.getChildren().size() % 20 == 0){
                    loadPosts();
                }
            });
        }
    }

    @FXML
    private void followButtonOnAction() {
        followUnfollowTask(followButton.isSelected());
    }

    public void followUnfollowTask(boolean action) {
        final Task<Void> followingTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if(action)
                    new UserDAOImpl().setFollow(user.getUsername());
                else
                    new UserDAOImpl().removeFollow(user.getUsername());
                return null;
            }
        };
        new Thread(followingTask).start();
        followingTask.setOnSucceeded(workerStateEvent -> {
            if (followButton.isSelected()) followButton.setText("UNFOLLOW");
            else followButton.setText("FOLLOW");
            getUserStatsTask();
        });

        ObservableList<User> followedUsers = ViewFactory.getInstance().getUser().getFollowedUsers();
        if(action)
            followedUsers.add(0, user);
        else
            followedUsers.removeIf(u -> u.getUsername().equals(user.getUsername()));
    }


    public void setUserInfo() {
        if (user.getAvatar() != null)
            avatar.setImage(user.getAvatar());

        name.setText(user.getName());
        this.username.setText("@" + user.getUsername());

        if (user.getBio() != null)
            bio.setText(user.getBio());

        checkFollowingTask();
    }


    public void checkFollowingTask() {
        final Task<Boolean> checkFollowing = new Task<>() {
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

    private void getUserStatsTask(){
        final Task<int[]> userStats = new Task<>() {
            @Override
            protected int[] call() throws Exception {
                return new UserDAOImpl().retrieveUserStats(user.getUsername());
            }
        };

        new Thread(userStats).start();
        userStats.setOnSucceeded(workerStateEvent -> {
            int[] stats = userStats.getValue();

            post.setText("Post: " + stats[0]);
            follower.setText("Follower: " + stats[1]);
            following.setText("Following: " + stats[2]);
        });

    }

    @Override
    public void loadPosts() {
        final Task<List<Post>> userPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getUsersPublicPosts(user.getUsername(), lastPostDate);
                if(posts != null)
                    lastPostDate = posts.get(posts.size() - 1).getDate();
                return posts;
            }
        };
        retrievePostsTask(userPostsTask);
        progressIndicator.visibleProperty().bind(userPostsTask.runningProperty());
    }


}
