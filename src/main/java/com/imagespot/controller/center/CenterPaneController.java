package com.imagespot.controller.center;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Post;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.net.URL;
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
    protected int offset;
    protected ChangeListener<Number> flowPaneResponsiveListener;

    public CenterPaneController() {
        offset = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdateOnAction();
        loadPosts();
        flowPaneResponsive(flowPane);
    }

    protected void btnUpdateOnAction() {
        btnUpdate.setOnAction(actionEvent -> {
            flowPane.getChildren().clear();
            offset = 0;
            loadPosts();
        });
    }

    protected abstract void loadPosts();

    protected void retrievePostsTask(Task<List<Post>> task) {
        ViewType type = ViewFactory.getInstance().getViewType();
        new Thread(task).start();
        task.setOnSucceeded(workerStateEvent -> {
            List<Post> posts = task.getValue();

            for (Post post : posts) {
                VBox postBox = ViewFactory.getInstance().getPostPreview(post, type);
                postBox.setId(String.valueOf(post.getIdImage()));
                flowPane.getChildren().add(postBox);
            }
            setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());

            if(flowPane.getChildren().isEmpty()) {
                flowPane.getChildren().add(nothingHereLabel());
                flowPane.setAlignment(Pos.CENTER);
            }
        });
    }

    protected void addPostsRemovedListener() {
        ViewFactory.getInstance().getUser().getPosts().addListener((ListChangeListener<Post>) change -> {
            change.next();

            if (change.wasRemoved() && change.getRemoved().size() == 1) {
                int id = change.getRemoved().get(0).getIdImage();
                flowPane.getChildren().removeIf(node -> node.getId().equals(String.valueOf(id)));
            }
        });
    }

    protected void addScrollPaneListener() {
        scrollPane.vvalueProperty().addListener((observableValue, number, scrollPosition) -> {
            if (scrollPosition.intValue() == 1 && offset % 20 == 0)  //scrollPosition == 1 -> scroll have reached the bottom
                loadPosts();
        });
    }

    protected void flowPaneResponsive(FlowPane fp) {
        flowPaneResponsiveListener = (observableValue, number, width) ->
                setFlowPaneChildWidth(fp.getChildren(), width.doubleValue());
        fp.widthProperty().addListener(flowPaneResponsiveListener);
    }


    protected void setFlowPaneChildWidth(List<Node> flowPaneChild, double flowPaneWidth) {
        for (Node box : flowPaneChild) {
            if(box instanceof VBox v){
                ImageView i = (ImageView) v.getChildren().get(0);

                int numNodeForRow = (int) (flowPaneWidth / 280);
                double nodeWidth = flowPaneWidth / numNodeForRow;

                v.setPrefWidth(nodeWidth - 1);
                i.setFitWidth(nodeWidth - 10);
            }
        }
    }

    private Label nothingHereLabel() {
        Label label = new Label();
        label.setPrefHeight(118);
        label.setPrefWidth(280);
        label.setText("THERE IS NOTHING HERE\n¯\\_(ツ)_/¯");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.valueOf("#7678ed"));
        label.setWrapText(true);

        Font font = new Font(24.0);
        label.setFont(font);

        return label;
    }
}
