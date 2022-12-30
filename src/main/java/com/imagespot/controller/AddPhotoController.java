package com.imagespot.controller;

import com.imagespot.DAOImpl.DeviceDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Device;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import static com.imagespot.Utils.Utils.*;


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

    private File file;
    private PostDAOImpl postDAO;
    private DeviceDAOImpl deviceDAO;

    private User user;
    private Device device;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL jsonFile = getClass().getResource("/json/labels.json");
        JSONObject jsonObject;

        if(jsonFile != null){
            try {
                jsonObject = new JSONObject(IOUtils.toString(jsonFile, StandardCharsets.UTF_8));

                JSONArray jsonCategories = jsonObject.getJSONArray("categories");
                JSONArray jsonDeviceTypes = jsonObject.getJSONArray("deviceType");
                JSONArray jsonStatus = jsonObject.getJSONArray("status");

                for(Object category: jsonCategories)
                    cbCategory.getItems().add(category.toString());

                for(Object deviceType: jsonDeviceTypes)
                    cbType.getItems().add(deviceType.toString());

                for(Object state: jsonStatus)
                    cbStatus.getItems().add(state.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("Error opening JSON file!");
        }

        user = ViewFactory.getInstance().getUser();
        cbStatus.getSelectionModel().selectFirst();
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
    private void btnPublishOnAction() throws SQLException, IOException {

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
            new PostDAOImpl().addPost(file, getRes(file), fldDescription.getText(),
                    getSize(file), getExt(file), timestamp, cbStatus.getValue(), device, user);
            btnPublish.setVisible(false);
            err.setText("DONE");
        }
    }
}
