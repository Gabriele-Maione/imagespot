package com.imagespot.Controller;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import java.util.List;

public class SearchedUsersController{
    @FXML
    private FlowPane flowPane;
    @FXML
    private ProgressIndicator progressIndicator;

    public void init(String searchedString) {
        searchUsersTask(searchedString);
    }

    private void searchUsersTask(String searchedString){
        Task<List<User>> searchedUsersTask =new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                return new UserDAOImpl().findUsers(searchedString);
            }
        };
        new Thread(searchedUsersTask).start();
        progressIndicator.visibleProperty().bind(searchedUsersTask.runningProperty());

        searchedUsersTask.setOnSucceeded(workerStateEvent -> {
            if(searchedUsersTask.getValue().size() == 0) flowPane.getChildren().add((new Label("I didn't found anything, try something else")));

            for (User user : searchedUsersTask.getValue()) {
                HBox postBox = ViewFactory.getInstance().getUserPreview(user);
                flowPane.getChildren().add(postBox);
            }
        });
    }
}
