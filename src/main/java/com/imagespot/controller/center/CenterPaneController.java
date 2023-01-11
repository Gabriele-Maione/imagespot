package com.imagespot.controller.center;

import com.imagespot.DAOImpl.BookmarkDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.Utils.Utils;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Post;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CenterPaneController implements Initializable {
    @FXML
    protected FlowPane flowPane;
    @FXML
    protected Label name;
    @FXML
    private Button btnUpdate;
    @FXML
    protected ProgressIndicator progressIndicator;
    @FXML
    protected ScrollPane scrollPane;
    private final ViewType type;
    protected Timestamp lastPostDate;


    public CenterPaneController() {
        this.type = ViewFactory.getInstance().getViewType();
        lastPostDate = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        loadPosts();

    }

    private void btnUpdateOnAction() {
        btnUpdate.setOnAction(actionEvent -> {
            flowPane.getChildren().clear();
            lastPostDate = null;
            loadPosts();
        });
    }

    protected void loadPosts() {   //TODO a sto punto si può cancellarlo, ma lo lascio magari ho rotto qualcosa nelle sottoclassi
        System.out.println("CENTER CONTROLLER TANTO NN VERRA' MAI CHIAMATO HAHA");
        final Task<List<Post>> getPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts;

                switch (type) {
                    case EXPLORE -> {
                        name.setText("Explore");
                        posts = new PostDAOImpl().getRecentPosts(lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        return posts;
                    }
                    case YOUR_GALLERY -> {
                        posts = new PostDAOImpl().getUserPosts(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        ViewFactory.getInstance().getUser().getPosts().addAll(posts);
                        return posts;
                    }
                    case FEED -> {
                        name.setText("Home");
                        posts = new PostDAOImpl().getFeed(ViewFactory.getInstance().getUser().getUsername(), lastPostDate);
                        if(posts != null)
                            lastPostDate = posts.get(posts.size() - 1).getDate();
                        return posts;
                    }
                    case FAVOURITES -> {
                        name.setText("Favourites");
                        new BookmarkDAOImpl().getUserBookmarks();
                        return ViewFactory.getInstance().getUser().getBookmarks();
                    }
                    default -> {
                        return null;
                    }
                }
            }
        };

        retrievePostsTask(getPostsTask);
        progressIndicator.visibleProperty().bind(getPostsTask.runningProperty());
    }

    public void retrievePostsTask(Task<List<Post>> task) {
        new Thread(task).start();
        task.setOnSucceeded(workerStateEvent -> {
            List<Post> posts = task.getValue();

            for (Post post : posts) {
                VBox postBox = ViewFactory.getInstance().getPostPreview(post);
                flowPane.getChildren().add(postBox);
            }
            flowPaneResponsive();
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
