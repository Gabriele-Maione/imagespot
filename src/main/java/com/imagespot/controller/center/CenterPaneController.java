package com.imagespot.controller.center;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Post;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public abstract class CenterPaneController implements Initializable {
    @FXML
    protected FlowPane flowPane;
    @FXML
    protected Label name;
    @FXML
    protected Button btnUpdate;
    @FXML
    protected ProgressIndicator progressIndicator;
    @FXML
    protected ScrollPane scrollPane;
    protected Timestamp lastPostDate;
    protected ListChangeListener<? super Post> postsListner;

    public CenterPaneController() {
        lastPostDate = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        loadPosts();
    }

    protected void btnUpdateOnAction() {
        btnUpdate.setOnAction(actionEvent -> {
            flowPane.getChildren().clear();
            lastPostDate = null;
            loadPosts();
        });
    }

    protected abstract void loadPosts();

    protected void retrievePostsTask(Task<List<Post>> task, boolean addListener) {
        new Thread(task).start();
        task.setOnSucceeded(workerStateEvent -> {
            List<Post> posts = task.getValue();

            for (Post post : posts) {
                VBox postBox = ViewFactory.getInstance().getPostPreview(post);
                postBox.setId(String.valueOf(post.getIdImage()));
                flowPane.getChildren().add(postBox);
            }
            flowPaneResponsive();

            if(addListener)
                addPostsListener();
        });
    }

    private void addPostsListener(){
        ViewType type = ViewFactory.getInstance().getViewType();

        postsListner = (ListChangeListener<Post>) change -> {
            change.next();

            if(type == ViewType.YOUR_GALLERY && change.wasAdded()){
                Post postAdded = change.getAddedSubList().get(0);
                VBox postBox = ViewFactory.getInstance().getPostPreview(postAdded);
                postBox.setId(String.valueOf(postAdded.getIdImage()));
                flowPane.getChildren().add(0, postBox);
            }

            if(change.wasRemoved()){
                int id = change.getRemoved().get(0).getIdImage();
                flowPane.getChildren().removeIf(node -> node.getId().equals(String.valueOf(id)));
            }
        };

        ViewFactory.getInstance().getUser().getPosts().addListener(postsListner);
    }

    protected void addScrollPaneListener(){
        scrollPane.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
            if(scrollPosition.intValue() == 1 && flowPane.getChildren().size() % 20 == 0){ //scrollPosition == 1 -> scroll have reached the bottom
                loadPosts();
            }
        });
    }

    private void flowPaneResponsive(){
        List<Node> flowPaneChildren = flowPane.getChildren();
        setWidthOfFlowPaneChild(flowPaneChildren, flowPane.getWidth());

        flowPane.widthProperty().addListener((observableValue, number, width) ->
                setWidthOfFlowPaneChild(flowPaneChildren, width.doubleValue()));
    }

    private void setWidthOfFlowPaneChild(List<Node> flowPaneChild, double flowPaneWidth){
        for(Node box : flowPaneChild){
            VBox v = (VBox)box;
            ImageView i = (ImageView)v.getChildren().get(0);

            int numNodeForRow = (int)(flowPaneWidth / 280);
            double nodeWidth = flowPaneWidth / numNodeForRow;

            v.setPrefWidth(nodeWidth - 1);
            i.setFitWidth(nodeWidth - 10);
        }
    }
}
