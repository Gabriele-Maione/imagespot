package com.imagespot.controller;

import com.imagespot.DAOImpl.DeviceDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.MainApplication;
import com.imagespot.model.Device;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.IdentityHashMap;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;


public class AddPhotoController implements Initializable {

    @FXML
    private Button btnPublish;

    @FXML
    private Button btnUpload;

    @FXML
    private ChoiceBox<String> cbCategory;

    @FXML
    private ChoiceBox<String> cbStatus;

    @FXML
    private ChoiceBox<String> cbType;

    @FXML
    private TextField fldBrand;

    @FXML
    private TextArea fldDescription;

    @FXML
    private TextField fldModel;

    @FXML
    private TextField fldSubject;

    @FXML
    private Hyperlink hlinkCancel;

    @FXML
    private ImageView img;
    @FXML
    private Label err;

    private String[] status = {"Public", "Private"};

    private String[] deviceT = {"Smartphone", "Digital Camera"};

    private String[] categories = {"Landscape", "Wildlife", "Macro", "Underwater",
            "Astrophotography", "Scientific", "Portrait", "Documentary", "Sport",
            "Fashion", "Commercial", "Street", "Event", "Travel", "Pet", "Food",
            "Architecture", "Family", "Other"};

    private File file;
    private PostDAOImpl postDAO;
    private DeviceDAOImpl deviceDAO;

    private User user;
    private Device device;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cbCategory.getItems().addAll(categories);
        cbType.getItems().addAll(deviceT);
        cbStatus.getItems().addAll(status);
        cbStatus.getSelectionModel().selectFirst();
    }

    protected void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void btnUploadOnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        file = fc.showOpenDialog(img.getScene().getWindow());
        if(file != null) {
            img.setImage(new Image((file.getAbsolutePath())));
            btnUpload.setText("Change");
        }
    }
    @FXML
    private void btnPublishOnAction() throws SQLException, FileNotFoundException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        device = new Device();
        deviceDAO = new DeviceDAOImpl();

        if (file == null)
            err.setText("LOAD A PHOTO!!!");
        else if (fldBrand.getText().isBlank() || fldModel.getText().isBlank() || cbType.getValue() == null)
            err.setText("SPECIFY DEVICE!!!");
        else {
            device.setIdDevice(deviceDAO.addDevice(fldBrand.getText(), fldModel.getText(), cbType.getValue()));
            device.setBrand(fldBrand.getText());
            device.setModel(fldModel.getText());
            device.setDeviceType(cbType.getValue());
            new PostDAOImpl().addPost(file, getRes(), fldDescription.getText(),
                    getSize(), getExt(), timestamp, cbStatus.getValue(), device, user);
            btnPublish.setVisible(false);
            err.setText("DONE");
        }
    }

    private String getRes() {
        Image i = new Image(file.getAbsolutePath());
        return ((int)i.getHeight() + "x" + (int)i.getWidth());
    }

    private String getExt() {
        return FilenameUtils.getExtension(file.getName());
    }
    private int getSize() {
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return (int)(file.length()/1024);
    }
}
