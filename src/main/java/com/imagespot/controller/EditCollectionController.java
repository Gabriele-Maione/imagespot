package com.imagespot.controller;

import com.imagespot.DAOImpl.CollectionDaoImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Collection;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Optional;

public class EditCollectionController {
    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button btnApply;
    @FXML
    private ProgressIndicator progressIndicator;
    private Collection collection;
    private CollectionDaoImpl collectionDao;

    public void init(Collection collection){
        this.collection = collection;
        name.setText(collection.getName());
        description.setText(collection.getDescription());
        progressIndicator.setVisible(false);
    }

    @FXML
    void btnDeleteOnAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Collection");
        alert.setHeaderText("Are you sure you want to delete this collection?");
        alert.setContentText("This action cannot be undone.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            Task<Void> deleteCollectionTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    new CollectionDaoImpl().deleteCollection(collection.getIdCollection());
                    return null;
                }
            };
            new Thread(deleteCollectionTask).start();
            progressIndicator.visibleProperty().bind(deleteCollectionTask.runningProperty());
            deleteCollectionTask.setOnSucceeded(workerStateEvent -> {
                ViewFactory.getInstance().getUser().getCollections().removeIf(c -> c.getIdCollection().equals(collection.getIdCollection()));
                Stage stage = (Stage)description.getScene().getWindow();
                stage.close();
            });
        }
    }

    @FXML
    void applyBtnOnAction(){
        final Task<Void> btnApplyTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String nameText = name.getText();
                if(!nameText.equals(collection.getName())){
                    new CollectionDaoImpl().setCollectionName(collection.getIdCollection(), nameText);
                    collection.setName(nameText);
                }
                String descriptionText = description.getText();
                if(!descriptionText.equals(collection.getDescription())){
                    descriptionText = descriptionText.isEmpty() ? null : descriptionText;
                    new CollectionDaoImpl().setCollectionDescription(collection.getIdCollection(), descriptionText);
                    collection.setDescription(descriptionText);
                }
                return null;
            }
        };
        new Thread(btnApplyTask).start();
        progressIndicator.visibleProperty().bind(btnApplyTask.runningProperty());
        btnApplyTask.setOnSucceeded(workerStateEvent -> {
            btnApply.setText("Done");
            int index = ViewFactory.getInstance().getUser().getCollections().indexOf(collection);
            ViewFactory.getInstance().getUser().getCollections().set(index, collection);
        });
    }

    @FXML
    void closeButtonOnAction() {
        Stage stage = (Stage) btnApply.getScene().getWindow();
        stage.close();
    }
}
