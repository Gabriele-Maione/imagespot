package com.imagespot.controller;

import com.imagespot.DAOImpl.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Collection;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AddPostsToCollectionController implements Initializable {
    @FXML
    private Label lblCollectionName;
    @FXML
    private Label lblSelectedYourGalleryPosts;
    @FXML
    private Label lblSelectedPostCollection;
    @FXML
    private ScrollPane scrollPanePostCollection;
    @FXML
    private ScrollPane scrollPaneYourGallery;
    @FXML
    private FlowPane flowPanePostCollection;
    @FXML
    private FlowPane flowPaneYourGallery;
    @FXML
    private ProgressIndicator progressIndicator1;
    @FXML
    private ProgressIndicator progressIndicator2;
    @FXML
    private Button btnCancel1;
    @FXML
    private Button btnCancel2;
    @FXML
    private Button btnRemovePosts;
    @FXML
    private Button btnAddPosts;
    private Collection collection;
    private User user;
    private HashMap<Integer, Post> posts;
    private ObservableList<Integer> selectedPostsToRemove;
    private ObservableList<Integer> selectedPostsToAdd;
    private int offsetPostsCollection;
    private int offsetYourGalleryPosts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = ViewFactory.getInstance().getUser();
        offsetPostsCollection = 0;
        offsetYourGalleryPosts = 0;
        selectedPostsToRemove = FXCollections.observableList(new ArrayList<>());
        selectedPostsToAdd = FXCollections.observableList(new ArrayList<>());
        posts = new HashMap<>();
        flowPanePostCollection.setVgap(5);
        flowPanePostCollection.setHgap(5);
        flowPaneYourGallery.setVgap(5);
        flowPaneYourGallery.setHgap(5);
        btnAddPosts.setDisable(true);
        btnRemovePosts.setDisable(true);
        btnCancel1.setDisable(true);
        btnCancel2.setDisable(true);
        addListeners();
    }

    public void init(Collection collection){
        this.collection = collection;

        if(this.collection != null){
            lblCollectionName.setText("Add posts to " + collection.getName() + " collection");
            loadUserPostsCollection();
            loadYourGalleryPosts();

            btnRemovePosts.setOnAction(event -> removePostsToCollection());
            btnAddPosts.setOnAction(event -> addPostsToCollection());

            btnCancel1.setOnAction(event -> {
                btnCancelOnAction(flowPanePostCollection);
                selectedPostsToRemove.clear();
            });

            btnCancel2.setOnAction(event -> {
                btnCancelOnAction(flowPaneYourGallery);
                selectedPostsToAdd.clear();
            });
        }
    }

    private void loadUserPostsCollection(){
        final Task<List<Post>> userPostsCollectionTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts = new CollectionDaoImpl().getUserPostsOfCollection(user.getUsername(), collection.getIdCollection(), (flowPanePostCollection.getChildren().size() - offsetPostsCollection) + offsetPostsCollection);
                offsetPostsCollection += posts.size();
                return posts;
            }
        };
        new Thread(userPostsCollectionTask).start();
        progressIndicator1.visibleProperty().bind(userPostsCollectionTask.runningProperty());
        userPostsCollectionTask.setOnSucceeded(workerStateEvent -> {
            for(Post p : userPostsCollectionTask.getValue()){
                posts.put(p.getIdImage(), p);
                flowPanePostCollection.getChildren().add(createPostPreview(p.getIdImage(), p.getPreview(), true));
            }
        });
    }

    private void loadYourGalleryPosts(){
        final Task<List<Post>> yourGalleryPostsTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                ArrayList<Post> posts = new CollectionDaoImpl().getUserGallery(user.getUsername(), collection.getIdCollection(), (flowPaneYourGallery.getChildren().size() - offsetYourGalleryPosts) + offsetYourGalleryPosts);
                offsetYourGalleryPosts += posts.size();
                return posts;
            }
        };
        new Thread(yourGalleryPostsTask).start();
        progressIndicator2.visibleProperty().bind(yourGalleryPostsTask.runningProperty());
        yourGalleryPostsTask.setOnSucceeded(workerStateEvent -> {
            for(Post p : yourGalleryPostsTask.getValue()){
                posts.put(p.getIdImage(), p);
                flowPaneYourGallery.getChildren().add(createPostPreview(p.getIdImage(), p.getPreview(), false));
            }
        });
    }

    private void removePostsToCollection(){
        if(!selectedPostsToRemove.isEmpty()){
            final Task<Void> removePostsToCollectionTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    new CollectionDaoImpl().removePostsToCollection(collection.getIdCollection(), selectedPostsToRemove);
                    return null;
                }
            };
            new Thread(removePostsToCollectionTask).start();
            removePostsToCollectionTask.setOnSucceeded(workerStateEvent -> {
                for(Integer id : selectedPostsToRemove){
                    flowPanePostCollection.getChildren().removeIf(node -> node.getId().equals(String.valueOf(id)));

                    Post p = posts.get(id);
                    flowPaneYourGallery.getChildren().add(0, createPostPreview(p.getIdImage(), p.getPreview(), false));
                }
                selectedPostsToRemove.clear();
            });
        }
    }

    private void addPostsToCollection(){
        if(!selectedPostsToAdd.isEmpty()){
            final Task<Void> addPostsToCollectionTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    new CollectionDaoImpl().addPostsToCollection(collection.getIdCollection(), selectedPostsToAdd);
                    return null;
                }
            };
            new Thread(addPostsToCollectionTask).start();
            addPostsToCollectionTask.setOnSucceeded(workerStateEvent -> {
                for(Integer id : selectedPostsToAdd){
                    flowPaneYourGallery.getChildren().removeIf(node -> node.getId().equals(String.valueOf(id)));

                    Post p = posts.get(id);
                    flowPanePostCollection.getChildren().add(0, createPostPreview(p.getIdImage(), p.getPreview(), true));
                }
                selectedPostsToAdd.clear();
            });
        }

    }

    private StackPane createPostPreview(int postId, Image image, boolean type){
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setId(String.valueOf(postId));

        stackPane.setPrefWidth(((type) ? flowPanePostCollection : flowPaneYourGallery).getWidth() / 5 - 8);

        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
        imageView.fitHeightProperty().bind(stackPane.prefWidthProperty());

        stackPane.getChildren().add(imageView);
        stackPane.setOnMouseClicked(event -> {
            if(type)
                collectionPostPreviewOnClick(stackPane, postId, imageView);
            else
                yourGalleryPostPreviewOnClick(stackPane, postId, imageView);
        });

        return stackPane;
    }

    private void collectionPostPreviewOnClick(StackPane stackPane, int postId, ImageView imageView){
        if(selectedPostsToRemove.contains(postId)){
            selectedPostsToRemove.removeIf(integer -> integer.equals(postId));
            stackPane.setPadding(new Insets(0));
            stackPane.setStyle("-fx-background-color: #f0f1f4");
            imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
            imageView.fitHeightProperty().bind(stackPane.prefWidthProperty());
        }
        else{
            selectedPostsToRemove.add(postId);
            stackPane.setPadding(new Insets(5));
            stackPane.setStyle("-fx-background-color: #bb0808");
            imageView.fitWidthProperty().bind(stackPane.prefWidthProperty().subtract(10));
            imageView.fitHeightProperty().bind(stackPane.prefWidthProperty().subtract(10));
        }
    }

    private void yourGalleryPostPreviewOnClick(StackPane stackPane, int postId, ImageView imageView){
        if(selectedPostsToAdd.contains(postId)){
            selectedPostsToAdd.removeIf(integer -> integer.equals(postId));
            stackPane.setPadding(new Insets(0));
            stackPane.setStyle("-fx-background-color: #f0f1f4");
            imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
            imageView.fitHeightProperty().bind(stackPane.prefWidthProperty());
        }
        else{
            selectedPostsToAdd.add(postId);
            stackPane.setPadding(new Insets(5));
            stackPane.setStyle("-fx-background-color: #afafe3");
            imageView.fitWidthProperty().bind(stackPane.prefWidthProperty().subtract(10));
            imageView.fitHeightProperty().bind(stackPane.prefWidthProperty().subtract(10));
        }
    }

    private void addListeners(){
        selectedPostsToRemove.addListener((ListChangeListener<? super Integer>) change -> {
            btnCancel1.setDisable(selectedPostsToRemove.isEmpty());
            btnRemovePosts.setDisable(selectedPostsToRemove.isEmpty());
            lblSelectedPostCollection.setText(selectedPostsToRemove.size() + " selected");
        });

        selectedPostsToAdd.addListener((ListChangeListener<? super Integer>) observable -> {
            btnCancel2.setDisable(selectedPostsToAdd.isEmpty());
            btnAddPosts.setDisable(selectedPostsToAdd.isEmpty());
            lblSelectedYourGalleryPosts.setText(selectedPostsToAdd.size() + " selected");
        });

        scrollPanePostCollection.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
            if(scrollPosition.intValue() == 1 && offsetPostsCollection % 20 == 0)
                loadUserPostsCollection();
        });

        scrollPaneYourGallery.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
            if(scrollPosition.intValue() == 1 && offsetYourGalleryPosts % 20 == 0)
                loadYourGalleryPosts();
        });
    }
    private void btnCancelOnAction(FlowPane flowPane){
        for(Node node : flowPane.getChildren()){
            StackPane stackPane = (StackPane) node;
            stackPane.setPadding(new Insets(0));
            stackPane.setStyle("-fx-background-color: #fdfdfe");
            ImageView imageView = (ImageView)stackPane.getChildren().get(0);
            imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
            imageView.fitHeightProperty().bind(stackPane.prefWidthProperty());
        }
    }
}