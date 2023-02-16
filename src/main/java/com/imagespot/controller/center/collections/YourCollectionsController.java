package com.imagespot.controller.center.collections;

import com.imagespot.DAOImpl.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.View.ViewType;
import com.imagespot.model.Collection;
import com.imagespot.model.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class YourCollectionsController extends CollectionsController {
    private User user;
    private ObjectProperty<Boolean> isLoading;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isLoading = new SimpleObjectProperty<>();
        user = ViewFactory.getInstance().getUser();
        super.initialize(url, resourceBundle);

        name.setText("Your own Collections");
        addCollectionListener();
    }

    @Override
    protected void loadPosts() {
        final Task<List<Collection>> getCollectionWhereUserPostedTask = new Task<>() {
            @Override
            protected ArrayList<Collection> call() throws Exception {
                ArrayList<Collection> collections = new CollectionDaoImpl().getOwnedCollections(user.getUsername(), (flowPane.getChildren().size() - offset) + offset);
                offset += collections.size();
                ViewFactory.getInstance().getUser().getCollections().addAll(collections);
                return collections;
            }
        };
        isLoading.bind(getCollectionWhereUserPostedTask.runningProperty());
        retrieveCollectionsTask(getCollectionWhereUserPostedTask);
    }

    private void addCollectionListener(){
        user.getCollections().addListener((ListChangeListener<Collection>) change -> {
            change.next();

            if(change.wasReplaced()){
                Collection collectionUpdated = change.getList().get(change.getFrom());

                if(collectionUpdated != null){
                    VBox collectionBox = ViewFactory.getInstance().getCollectionPreview(collectionUpdated, ViewType.YOUR_COLLECTIONS);
                    collectionBox.setId(String.valueOf(collectionUpdated.getIdCollection()));
                    flowPane.getChildren().set(user.getCollections().indexOf(collectionUpdated), collectionBox);
                }
            }
            else if(change.wasAdded() && !isLoading.getValue()){
                Collection c = change.getAddedSubList().get(0);
                VBox collectionBox = ViewFactory.getInstance().getCollectionPreview(c, ViewType.YOUR_COLLECTIONS);
                collectionBox.setId(String.valueOf(c.getIdCollection()));

                if(flowPane.getChildren().size() == 1 && flowPane.getChildren().get(0) instanceof Label){
                    flowPane.getChildren().clear();
                    flowPane.setAlignment(Pos.TOP_LEFT);
                }

                flowPane.getChildren().add(0, collectionBox);
            }
            else if(change.wasRemoved() && change.getRemoved().size() == 1){
                int id = change.getRemoved().get(0).getIdCollection();
                flowPane.getChildren().removeIf(node -> node.getId().equals(String.valueOf(id)));
            }

            setFlowPaneChildWidth(flowPane.getChildren(), flowPane.getWidth());
        });
    }

}
