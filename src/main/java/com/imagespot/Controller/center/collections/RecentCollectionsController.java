package com.imagespot.Controller.center.collections;

import com.imagespot.DAOImpl.CollectionDaoImpl;
import com.imagespot.Model.Collection;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RecentCollectionsController extends CollectionsController{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        name.setText("Recent added Collections");
    }

    @Override
    protected void loadPosts() {
        final Task<List<Collection>> getRecentCollectionTask = new Task<>() {
            @Override
            protected ArrayList<Collection> call() throws Exception {
                ArrayList<Collection> collections = new CollectionDaoImpl().getRecentCollections(offset);
                offset += collections.size();
                return collections;
            }
        };
        retrieveCollectionsTask(getRecentCollectionTask);
    }
}
