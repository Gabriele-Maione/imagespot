package com.imagespot.Controller.center.collections;

import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.Controller.center.CenterPaneController;
import com.imagespot.Model.Collection;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public abstract class CollectionsController extends CenterPaneController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        addScrollPaneListener();
    }

    protected void retrieveCollectionsTask(Task<List<Collection>> task){
        ViewType type = ViewFactory.getInstance().getViewType();
        new Thread(task).start();
        progressIndicator.visibleProperty().bind(task.runningProperty());
        btnUpdate.disableProperty().bind(task.runningProperty());
        task.setOnSucceeded(workerStateEvent -> {
            List<Collection> collections = task.getValue();

            if(!collections.isEmpty()){
                flowPane.setAlignment(Pos.TOP_LEFT);
                for(Collection collection : collections){
                    VBox collectionBox = ViewFactory.getInstance().getCollectionPreview(collection, type);
                    collectionBox.setId(String.valueOf(collection.getIdCollection()));
                    flowPane.getChildren().add(collectionBox);
                }
                setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());
            }

            if(flowPane.getChildren().isEmpty()){
                flowPane.getChildren().add(nothingHereLabel());
                flowPane.setAlignment(Pos.CENTER);
            }
        });
    }
}
