package com.imagespot.Controller;

import com.imagespot.ImplementationPostgresDAO.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.User;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import static com.imagespot.Utils.Utils.crop;
import static com.imagespot.Utils.Utils.setAvatarRounde;

public class SettingsController implements Initializable {
    @FXML
    private TextArea bio;

    @FXML
    private Button btnApply;

    @FXML
    private ChoiceBox<String> cbGender;

    @FXML
    private TextField fldName;

    @FXML
    private Hyperlink hlinkSkip;

    @FXML
    private ImageView imgPreview;

    @FXML
    private TextField fldCustom;
    @FXML
    private Button deleteBtn;

    private File avatar;
    private boolean changedAvatarFlag = false;
    private boolean deletedAvatarFlag = false;

    private final String[] gender = {"Male", "Female", "Not binary", "Prefer not say", "Custom"};

    private User user;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user = ViewFactory.getInstance().getUser();
        setAvatarRounde(imgPreview);
        if (user.getAvatar() != null) {
            deleteBtn.setVisible(true);
            imgPreview.setImage(user.getAvatar());
        } else deleteBtn.setVisible(false);


        fldName.setText(user.getName());
        bio.setText(user.getBio());
        choiceBoxInit();
    }

    public void choiceBoxInit() {
        cbGender.getItems().addAll(gender);
        cbGender.setValue(user.getGender());
        cbGender.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (cbGender.getValue().equals("Custom")) {
                        fldCustom.setVisible(true);
                        cbGender.valueProperty().bind(fldCustom.textProperty());
                    } else if (!newValue.equals(fldCustom.getText())) {
                        cbGender.valueProperty().unbind();
                        cbGender.setValue(newValue);
                        fldCustom.setVisible(false);
                        cbGender.valueProperty().unbind();
                    }
                });
    }


    @FXML
    private void btnAvatarOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        avatar = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if (avatar != null) {
            imgPreview.setImage(crop(new Image((avatar.getAbsolutePath()))));
            changedAvatarFlag = true;
        }
    }

    @FXML
    private void btnDeleteOnAction() {

        if (deleteBtn.getText().equals("Delete")) {
            imgPreview.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/bear_icon.png"))));
            deletedAvatarFlag = true;
            avatar = null;
            deleteBtn.setText("Undo");
        } else if (deleteBtn.getText().equals("Undo")) {
            imgPreview.setImage(user.getAvatar());
            deleteBtn.setText("Delete");
            deletedAvatarFlag = false;
        }
    }


    private void updateInfoTask() {
        final Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Loading...");
                UserDAOImpl userDB = new UserDAOImpl();
                if (changedAvatarFlag)
                    userDB.setAvatar(user.getUsername(), avatar);
                else if (deletedAvatarFlag)
                    userDB.deleteAvatar(user.getUsername());

                userDB.setName(user.getUsername(), fldName.getText());
                userDB.setGender(user.getUsername(), cbGender.getValue());
                userDB.setBio(user.getUsername(), bio.getText());
                return null;
            }
        };
        new Thread(updateTask).start();
        btnApply.textProperty().bind(updateTask.messageProperty());
        updateTask.setOnSucceeded(workerStateEvent -> {
            ViewFactory.getInstance().getUser().setBio(bio.getText());
            ViewFactory.getInstance().getUser().setGender(cbGender.getValue());
            ViewFactory.getInstance().getUser().setName(fldName.getText());
            if (avatar != null) {
                ViewFactory.getInstance().getUser().setAvatar((new Image(avatar.getAbsolutePath())));
                deleteBtn.setVisible(true);
                deleteBtn.setText("Delete");
            } else if(deletedAvatarFlag){
                ViewFactory.getInstance().getUser().setAvatar(null);
                deleteBtn.setVisible(false);
            }
            btnApply.textProperty().unbind();
            btnApply.setText("DONE!");
            changedAvatarFlag = false;
            deletedAvatarFlag = false;
        });
    }

    @FXML
    private void closeButtonOnAction() {
        Stage stage = (Stage) hlinkSkip.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void dragged(MouseEvent ev) {
        Stage stage = (Stage) fldName.getScene().getWindow();
        stage.setX(ev.getScreenX() - x);
        stage.setY(ev.getScreenY() - y);
    }

    @FXML
    private void pressed(MouseEvent ev) {
        x = ev.getSceneX();
        y = ev.getSceneY();
    }

    @FXML
    private void applyBtnOnAction() {
        updateInfoTask();
    }
}
