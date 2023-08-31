package com.imagespot.Controller.center.collections;

import com.imagespot.ImplementationPostgresDAO.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.Collection;
import com.imagespot.Model.User;
import javafx.concurrent.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsedCollectionsController extends CollectionsController{

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        user = ViewFactory.getInstance().getUser();
        name.setText("Collections you recently posted in");
    }

    @Override
    protected void loadPosts() {
        final Task<List<Collection>> getCollectionWhereUserPostedTask = new Task<>() {
            @Override
            protected ArrayList<Collection> call() throws Exception {
                ArrayList<Collection> collections = new CollectionDaoImpl().getCollectionsWhereUserPosted(user.getUsername(), offset);
                offset += collections.size();
                return collections;
            }
        };
        retrieveCollectionsTask(getCollectionWhereUserPostedTask);
    }
}
