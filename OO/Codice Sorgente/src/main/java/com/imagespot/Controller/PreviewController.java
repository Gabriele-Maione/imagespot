package com.imagespot.Controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Model.Post;
import com.imagespot.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import static com.imagespot.Utils.TimeUtil.toDuration;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class PreviewController {


    @FXML
    private ImageView image_preview;
    @FXML
    private Label username;
    @FXML
    private Label name;
    @FXML
    private ImageView avatar;
    @FXML
    private Label passedTime;
    @FXML
    private VBox preview;
    @FXML
    private Button modify;
    private Post post;
    private ViewType viewType;
    private User userPreview;


    public void setData(Post pst, ViewType type) {
        this.post = pst;
        this.viewType = type;
        image_preview.setImage(post.getPreview());
        image_preview.fitWidthProperty().bind(preview.prefWidthProperty().subtract(1));
        image_preview.fitHeightProperty().bind(preview.prefWidthProperty().subtract(1));
        userPreview = post.getProfile();

        if(userPreview != null){
            name.setText(userPreview.getName());
            username.setText("@" + userPreview.getUsername());
            if (userPreview.getAvatar() != null) {
                avatar.setImage(userPreview.getAvatar());
                setAvatarRounde(avatar);
            }
        }


        passedTime.setText(toDuration(System.currentTimeMillis() - post.getDate().getTime()));
        setModify();
    }

    private void setModify() {
        if(viewType.equals(ViewType.YOUR_GALLERY)){
            modify.setDisable(false);
            modify.setVisible(true);
        }
    }

    @FXML
    private void ModifyBtnOnAction() {
        ViewFactory.getInstance().showEditPostWindow(post);
    }

    @FXML
    private void previewOnClick() {
        username.getScene().setRoot(ViewFactory.getInstance().getPostView(post.getIdImage()));
    }

    @FXML
    private void userOnClick() {
        if(userPreview != null){
            ViewFactory.getInstance().setViewType(ViewType.PROFILE);
            BorderPane borderPane = (BorderPane) image_preview.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getUserPage(post.getProfile()));
        }
    }
}
