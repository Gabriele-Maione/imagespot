package com.imagespot.controller.center;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.TaggedUserDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static com.imagespot.Utils.Utils.setAvatarRounde;

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
    private Button postBtn;
    @FXML
    private Button tagBtn;

    public User user;
    protected FlowPane flowPanePosts;
    private FlowPane flowPaneTag;
    private int offsetUserPosts;
    private int offsetUserTags;
    private enum userPageViewType {USER_PROFILE_POSTS, USER_PROFILE_TAG}
    private userPageViewType type;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        type = userPageViewType.USER_PROFILE_POSTS;
        offsetUserPosts = 0;
        offsetUserTags = 0;
        flowPanePosts = new FlowPane();
        flowPaneTag = new FlowPane();
        ((VBox) scrollPane.getContent()).getChildren().add(flowPanePosts);
        flowPane = flowPanePosts;
        flowPaneResponsive(flowPanePosts);
        flowPaneResponsive(flowPaneTag);
        addScrollPaneListener();
    }

    public void init(User user) throws SQLException {
        this.user = user;

        if (this.user != null) {
            setUserInfo();
            getUserStatsTask();
            loadPosts();
            if (user.getUsername().equals(ViewFactory.getInstance().getUser().getUsername())) {
                followButton.setText("SETTINGS");
                followButton.setOnAction(actionEvent -> settingsButtonOnAction());
            }
            else
                followButton.setOnAction(actionEvent -> followButtonOnAction());
        }
    }

    @FXML
    private void postBtnOnAction() {
        if (type != userPageViewType.USER_PROFILE_POSTS) {
            switchUserProfileView();
            type = userPageViewType.USER_PROFILE_POSTS;
        }
        loadPosts();
    }

    @FXML
    private void tagBtnOnAction() {
        if (type != userPageViewType.USER_PROFILE_TAG) {
            switchUserProfileView();
            type = userPageViewType.USER_PROFILE_TAG;
        }
        loadPosts();
    }

    private void switchUserProfileView() {
        VBox scrollPaneVbox = (VBox) scrollPane.getContent();
        scrollPaneVbox.getChildren().remove(flowPane);
        if (type == userPageViewType.USER_PROFILE_POSTS) {
            flowPane = flowPaneTag;
            offsetUserPosts = offset;
            offset = offsetUserTags;
            tagBtn.getStyleClass().add("btn-switch-view-selected");
            postBtn.getStyleClass().remove("btn-switch-view-selected");
        } else {
            flowPane = flowPanePosts;
            offsetUserTags = offset;
            offset = offsetUserPosts;
            postBtn.getStyleClass().add("btn-switch-view-selected");
            tagBtn.getStyleClass().remove("btn-switch-view-selected");
        }
        scrollPaneVbox.getChildren().add(flowPane);
    }

    private void followButtonOnAction() {
        followUnfollowTask(followButton.isSelected());
    }

    private void settingsButtonOnAction() {
        ViewFactory.getInstance().showSettingsWindow();
    }

    public void followUnfollowTask(boolean action) {
        final Task<Void> followingTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (action)
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
        if (action)
            followedUsers.add(0, user);
        else
            followedUsers.removeIf(u -> u.getUsername().equals(user.getUsername()));
    }

    public void setUserInfo() {
        if (user.getAvatar() != null) {
            avatar.setImage(user.getAvatar());
            setAvatarRounde(avatar);
        }

        name.setText(user.getName());
        this.username.setText("@" + user.getUsername());

        bio.setText(user.getBio() != null ? user.getBio() : "");

        if(!user.getUsername().equals(ViewFactory.getInstance().getUser().getUsername()))
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
            } else {
                followButton.setSelected(false);
                followButton.setText("FOLLOW");
            }
        });
    }

    private void getUserStatsTask() {
        final Task<int[]> userStats = new Task<>() {
            @Override
            protected int[] call() {
                return new UserDAOImpl().retrieveUserStats(user.getUsername());
            }
        };

        new Thread(userStats).start();
        userStats.setOnSucceeded(workerStateEvent -> {
            int[] stats = userStats.getValue();

            post.setText(String.valueOf(stats[0]));
            follower.setText(String.valueOf(stats[1]));
            following.setText(String.valueOf(stats[2]));
        });

    }

    @Override
    protected void loadPosts() {
        if (type == userPageViewType.USER_PROFILE_POSTS)
            loadUserPosts();
        else
            loadUserTags();
    }

    private void loadUserPosts() {
        final Task<List<Post>> userPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts = new PostDAOImpl().getUsersPublicPosts(user.getUsername(), offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(userPostsTask);
        postBtn.disableProperty().bind(userPostsTask.runningProperty());
        tagBtn.disableProperty().bind(userPostsTask.runningProperty());
        progressIndicator.visibleProperty().bind(userPostsTask.runningProperty());
    }

    private void loadUserTags() {
        final Task<List<Post>> userTagsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts = new TaggedUserDAOImpl().getTag(user.getUsername(), offset);
                offset += posts.size();
                return posts;
            }
        };
        retrievePostsTask(userTagsTask);
        postBtn.disableProperty().bind(userTagsTask.runningProperty());
        tagBtn.disableProperty().bind(userTagsTask.runningProperty());
        progressIndicator.visibleProperty().bind(userTagsTask.runningProperty());
    }
}
