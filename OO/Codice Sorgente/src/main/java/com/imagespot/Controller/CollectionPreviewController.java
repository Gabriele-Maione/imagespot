package com.imagespot.Controller;

import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Model.Collection;
import com.imagespot.Model.Post;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.imagespot.Utils.Utils.getCollectionPreview;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class CollectionPreviewController implements Initializable {
    @FXML
    private FlowPane flowPaneCollection;
    @FXML
    private Label lblCollectionName;
    @FXML
    private Label lblCollectionPosts;
    @FXML
    private Label lblCollectionMembers;
    @FXML
    private Label lblName;
    @FXML
    private Label lblUsername;
    @FXML
    private ImageView avatar;
    @FXML
    private Button btnEditCollection;
    @FXML
    private StackPane stackPane;
    @FXML
    private VBox collectionVBox;
    private Collection collection;
    private ViewType viewType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackPane.setOnMouseClicked(event -> {
            BorderPane borderPane = (BorderPane) stackPane.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getCollectionPostsView(collection));
        });
    }

    public void setData(Collection collection, ViewType type){
        this.collection = collection;
        this.viewType = type;
        lblCollectionName.setText(collection.getName());
        lblCollectionPosts.setText(String.valueOf(collection.getPostsCount()));
        lblCollectionMembers.setText(String.valueOf(collection.getMemberCount()));
        lblName.setText(collection.getOwner().getName());
        lblUsername.setText("@" + collection.getOwner().getUsername());
        if(collection.getOwner().getAvatar() != null){
            avatar.setImage(collection.getOwner().getAvatar());
            setAvatarRounde(avatar);
        }
        setModify();

        ImageView collectionImageView;

        if(collection.getPosts().size() == 4){
            ArrayList<Image> previews = new ArrayList<>();
            for (Post p: collection.getPosts())
                previews.add(p.getPreview());

            collectionImageView = new ImageView(getCollectionPreview(previews));
        }
        else{
            collectionImageView = new ImageView((collection.getPosts().isEmpty()) ?
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/placeholder.jpg"))) :
                collection.getPosts().get(0).getPreview()
            );
        }

        collectionImageView.fitWidthProperty().bind(collectionVBox.prefWidthProperty().subtract(1));
        collectionImageView.fitHeightProperty().bind(collectionVBox.prefWidthProperty().subtract(1));
        flowPaneCollection.getChildren().add(collectionImageView);

        Color darkOpacity = Color.color(0, 0, 0, 0.5);
        Rectangle rect = new Rectangle(0 ,0, darkOpacity);
        rect.widthProperty().bind(collectionVBox.prefWidthProperty().subtract(1));
        rect.heightProperty().bind(collectionVBox.prefWidthProperty().subtract(1));
        stackPane.getChildren().add(1, rect);
        stackPane.setAlignment(Pos.CENTER_LEFT);
    }

    private void setModify() {
        if(viewType.equals(ViewType.YOUR_COLLECTIONS)){
            btnEditCollection.setDisable(false);
            btnEditCollection.setVisible(true);
        }
    }

    @FXML
    private void btnEditOnClick(){
        ViewFactory.getInstance().showEditCollectionWindow(collection);
    }

    @FXML
    private void userOnClick() {
        ViewFactory.getInstance().setViewType(ViewType.PROFILE);
        BorderPane borderPane = (BorderPane) avatar.getScene().getRoot();
        borderPane.setCenter(ViewFactory.getInstance().getUserPage(collection.getOwner()));
    }
}

