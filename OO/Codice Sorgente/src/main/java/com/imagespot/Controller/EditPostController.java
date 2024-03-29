package com.imagespot.Controller;

import com.imagespot.ImplementationPostgresDAO.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.Post;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Optional;

public class EditPostController {
    @FXML
    private Button btnApply;
    @FXML
    private Hyperlink cancel;
    @FXML
    private Button deleteBtn;
    @FXML
    private TextArea description;
    @FXML
    private ImageView imgPreview;
    @FXML
    private HBox photo;
    @FXML
    private ChoiceBox<String> photoStatus;
    @FXML
    private ProgressIndicator loadingIndicator;
    private final String[] status = {"Public", "Private"};
    private Post post;

    public void init(Post post) throws SQLException {
        this.post = post;
        photoStatus.getItems().addAll(status);
        getData();
    }

    private void getData() {
        final Task<Void> getDataTask = new Task<>() {
            @Override
            protected Void call() {
                new PostDAOImpl().getDataForEdit(post);
                return null;
            }
        };
        new Thread(getDataTask).start();
        getDataTask.setOnSucceeded(workerStateEvent -> {
            description.setText(post.getDescription());
            photoStatus.setValue(post.getStatus());

            getPhoto();
        });
    }

    private void getPhoto() {
        final Task<Void> getPhotoTask = new Task<>() {
            @Override
            protected Void call() {
                if(post.getPhoto() == null)
                    post.setPhoto(new PostDAOImpl().getPhoto(post.getIdImage()));
                return null;
            }
        };
        loadingIndicator.visibleProperty().bind(getPhotoTask.runningProperty());
        new Thread(getPhotoTask).start();
        getPhotoTask.setOnSucceeded(workerStateEvent -> imgPreview.setImage(post.getPhoto()));
    }

    @FXML
    void applyBtnOnAction() {
        final Task<Void> btnApplyTask = new Task<>() {
            @Override
            protected Void call() {
                if(post.getDescription() != null && !description.getText().equals(post.getDescription())) {
                    String desc = description.getText().isEmpty() ? null : description.getText();
                    new PostDAOImpl().setDescription(post.getIdImage(), desc);
                    post.setDescription(desc);
                }

                if(!photoStatus.getValue().equals(post.getStatus())) {
                    new PostDAOImpl().setStatus(post.getIdImage(), photoStatus.getValue());
                    post.setStatus(photoStatus.getValue());
                }
                return null;
            }
        };
        new Thread(btnApplyTask).start();
        btnApplyTask.setOnSucceeded(workerStateEvent -> btnApply.setText("Done"));
    }

    @FXML
    void btnDeleteOnAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Photo");
        alert.setHeaderText("Are you sure you want to delete this photo?");
        alert.setContentText("This action cannot be undone.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() {
                    new PostDAOImpl().deletePost(post.getIdImage());
                    return null;
                }
            };
            new Thread(deleteTask).start();
            deleteTask.setOnSucceeded(workerStateEvent -> {
                ViewFactory.getInstance().getUser().removePost(post.getIdImage());
                Stage stage = (Stage)description.getScene().getWindow();
                stage.close();
            });
        }
    }

    @FXML
    void closeButtonOnAction() {
        Stage stage = (Stage) btnApply.getScene().getWindow();
        stage.close();
    }

}
