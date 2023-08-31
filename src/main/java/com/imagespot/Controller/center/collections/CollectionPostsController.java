package com.imagespot.Controller.center.collections;

import com.imagespot.ImplementationPostgresDAO.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Controller.center.CenterPaneController;
import com.imagespot.Model.Collection;
import com.imagespot.Model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class CollectionPostsController extends CenterPaneController {
    @FXML
    private Label lblCollectionName;
    @FXML
    private Label lblDescription;
    @FXML
    private ImageView ownerAvatar;
    @FXML
    private Label lblOwnerName;
    @FXML
    private Label lblOwnerUsername;
    @FXML
    private Label lblPostSize;
    @FXML
    private Label lblMemberSize;
    @FXML
    private ProgressIndicator progressIndicator;
    private Collection collection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        flowPaneResponsive(flowPane);
        addScrollPaneListener();
    }

    @Override
    protected void btnUpdateOnAction() {
       btnUpdate.setOnAction(actionEvent -> {
           flowPane.getChildren().clear();
           offset = 0;
           loadPosts();
           retrieveCollectionStats();
       });
    }

    public void init(Collection collection) {
        this.collection = collection;

        if(this.collection != null){
            lblCollectionName.setText(collection.getName());
            lblDescription.setText(collection.getDescription());
            ownerAvatar.setImage(collection.getOwner().getAvatar());
            setAvatarRounde(ownerAvatar);
            lblOwnerName.setText(collection.getOwner().getName());
            lblOwnerUsername.setText("@" + collection.getOwner().getUsername());
            lblPostSize.setText(String.valueOf(collection.getPostsCount()));
            lblMemberSize.setText(String.valueOf(collection.getMemberCount()));
            loadPosts();
        }
    }

    @Override
    protected void loadPosts() {
        final Task<List<Post>> collectionPostsTask = new Task<>() {
            @Override
            protected ArrayList<Post> call() throws Exception {
                ArrayList<Post> collectionPosts = new CollectionDaoImpl().getPostsOfCollection(collection.getIdCollection(), offset);
                offset += collectionPosts.size();
                return collectionPosts;
            }
        };
        progressIndicator.visibleProperty().bind(collectionPostsTask.runningProperty());
        btnUpdate.disableProperty().bind(collectionPostsTask.runningProperty());
        retrievePostsTask(collectionPostsTask);
    }

    private void retrieveCollectionStats(){
        final Task<int[]> collectionStatsTask = new Task<>() {
            @Override
            protected int[] call() throws Exception {
                return new CollectionDaoImpl().getCollectionStats(collection.getIdCollection());
            }
        };
        new Thread(collectionStatsTask).start();
        collectionStatsTask.setOnSucceeded(workerStateEvent -> {
            int[] stats = collectionStatsTask.getValue();

            collection.setPostsCount(stats[0]);
            collection.setMemberCount(stats[1]);
            lblPostSize.setText(String.valueOf(collection.getPostsCount()));
            lblMemberSize.setText(String.valueOf(collection.getMemberCount()));
        });
    }

    @FXML
    private void showAddPostsToCollectionWindow(){
        ViewFactory.getInstance().showAddPostsToCollectionWindow(collection);
    }

    @FXML
    private void userOnClick() {
        ViewFactory.getInstance().setViewType(ViewType.PROFILE);
        BorderPane borderPane = (BorderPane) ownerAvatar.getScene().getRoot();
        borderPane.setCenter(ViewFactory.getInstance().getUserPage(collection.getOwner()));
    }
}
