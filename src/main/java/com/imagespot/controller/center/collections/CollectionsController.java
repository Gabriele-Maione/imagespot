package com.imagespot.controller.center.collections;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.controller.center.CenterPaneController;
import com.imagespot.model.Collection;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public abstract class CollectionsController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        addScrollPaneListener();
    }

    protected void retrieveCollectionsTask(Task<List<Collection>> task){
        ViewType type = ViewFactory.getInstance().getViewType();
        new Thread(task).start();
        progressIndicator.visibleProperty().bind(task.runningProperty());
        btnUpdate.disableProperty().bind(task.runningProperty());
        task.setOnSucceeded(workerStateEvent -> {
            for(Collection collection : task.getValue()){
                VBox collectionBox = ViewFactory.getInstance().getCollectionPreview(collection, type);
                collectionBox.setId(String.valueOf(collection.getIdCollection()));
                flowPane.getChildren().add(collectionBox);
            }
            setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());

            if(flowPane.getChildren().isEmpty()){
                flowPane.getChildren().add(nothingHereLabel());
                flowPane.setAlignment(Pos.CENTER);
            }
        });
    }

    @Override
    protected void setFlowPaneChildWidth(List<Node> flowPaneChild, double flowPaneWidth) {
        for(Node box : flowPaneChild){
            VBox v = (VBox)box;

            int numNodeForRow = (int)(flowPaneWidth / 280);
            double nodeWidth = flowPaneWidth / numNodeForRow;

            v.setPrefWidth(nodeWidth - 10);
        }
    }
}
