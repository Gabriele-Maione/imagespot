package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import org.ocpsoft.prettytime.PrettyTime;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PostController implements Initializable {

    @FXML
    private ImageView avatar;

    @FXML
    private Button btnFollow;

    @FXML
    private Label description;

    @FXML
    private Label name;

    @FXML
    private ImageView photo;
    @FXML
    private Pane imgContainer;
    @FXML
    private Label username;
    @FXML
    private Button btnClose;

    @FXML
    private Post post;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void init(int idpost) throws SQLException {

        this.post = new PostDAOImpl().getPost(idpost);
        photo.fitWidthProperty().bind(imgContainer.widthProperty());
        photo.fitHeightProperty().bind(imgContainer.heightProperty());

        photo.setImage(new Image(post.getPhoto()));
        name.setText(post.getProfile().getName());
        username.setText("@" + post.getProfile().getUsername());
        if (post.getProfile().getAvatar() != null)
            avatar.setImage(new Image(post.getProfile().getAvatar()));
        description.setText(post.getDescription());
    }

    @FXML
    public void buttonCloseOnAction() {

        username.getScene().setRoot(ViewFactory.getInstance().getHomeRoot());
    }
}
