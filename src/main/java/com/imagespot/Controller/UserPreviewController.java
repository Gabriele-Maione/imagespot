package com.imagespot.Controller;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class UserPreviewController{
    @FXML
    private ImageView avatar;
    @FXML
    private HBox hboxUser;
    @FXML
    private Label name;
    @FXML
    private Label username;
    private User user;


    public void init(User user) {
        this.user = user;
        if (user.getAvatar() != null) {
            avatar.setImage(user.getAvatar());
            setAvatarRounde(avatar);
        }
        name.setText(user.getName());
        username.setText("@" + user.getUsername());
    }

    public void userPreviewOnClick() {
        ViewFactory.getInstance().setViewType(ViewType.USER_PROFILE);
        BorderPane borderPane = (BorderPane) hboxUser.getScene().getRoot();
        borderPane.setCenter(ViewFactory.getInstance().getUserPage(user));
        if(borderPane.lookup(".selected") != null)
            borderPane.lookup(".selected").getStyleClass().remove(0);
    }

}
