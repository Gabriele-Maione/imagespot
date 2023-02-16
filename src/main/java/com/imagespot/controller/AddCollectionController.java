package com.imagespot.controller;

import com.imagespot.DAOImpl.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Collection;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AddCollectionController implements Initializable {
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private TextField textFieldName;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label err;
    @FXML
    private Button btnCreate;
    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.setManaged(false);

        btnCreate.setOnAction(event -> {
            String name = textFieldName.getText().trim();
            String description = textAreaDescription.getText().isEmpty() ? null : textAreaDescription.getText().trim();
            String owner = ViewFactory.getInstance().getUser().getUsername();
            createCollectionTask(name, description, owner);
        });
    }

    private void createCollectionTask(String name, String description, String owner){
        if(textFieldName.getText().isBlank())
            err.setText("Enter the Collection Name!!!");
        else{
            final Task<Integer> createCollectionTask = new Task<>() {
                @Override
                protected Integer call() throws Exception {
                    return new CollectionDaoImpl().createCollection(name, description, owner);
                }
            };
            new Thread(createCollectionTask).start();
            progressIndicator.setManaged(true);
            progressIndicator.visibleProperty().bind(createCollectionTask.runningProperty());
            createCollectionTask.setOnSucceeded(workerStateEvent -> {
                progressIndicator.setManaged(false);
                btnCreate.setVisible(false);
                err.setText("DONE");
                Collection addedCollection = new Collection(createCollectionTask.getValue(), name, description, ViewFactory.getInstance().getUser());
                ViewFactory.getInstance().getUser().getCollections().add(0, addedCollection);
            });
        }
    }

    @FXML
    private void closeButtonOnAction() {
        Stage stage = (Stage) textFieldName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void dragged(MouseEvent ev) {
        Stage stage = (Stage) textFieldName.getScene().getWindow();
        stage.setX(ev.getScreenX() - x);
        stage.setY(ev.getScreenY() - y);
    }

    @FXML
    private void pressed(MouseEvent ev) {
        x = ev.getSceneX();
        y = ev.getSceneY();
    }
}
