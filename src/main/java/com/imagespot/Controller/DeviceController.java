package com.imagespot.Controller;

import com.imagespot.ImplementationPostgresDAO.DeviceDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.Device;
import com.imagespot.Model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class DeviceController implements Initializable {
    @FXML
    private Label lblDeviceSelected;
    @FXML
    private SplitPane splitPaneDevices;
    @FXML
    private VBox smartphoneVBox;
    @FXML
    private VBox digitalCameraVBox;
    @FXML
    private Button btnAddSmartphone;
    @FXML
    private Button btnAddDigitalCamera;
    @FXML
    private Button btnConfirmNewDigitalCamera;
    @FXML
    private Button btnCancelNewSmartphone;
    @FXML
    private Button btnCancelNewDigitalCamera;
    @FXML
    private HBox hBoxNewSmartphone;
    @FXML
    private HBox hBoxNewDigitalCamera;

    private User user;

    private Device device;
    private ArrayList<Device> recentUserDevices;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        device = null;
        setHboxVisibility(hBoxNewSmartphone, false);
        setHboxVisibility(hBoxNewDigitalCamera, false);

        user = ViewFactory.getInstance().getUser();
        getRecentUsedDevicesTask(user.getUsername());
    }

    @FXML
    private void confirmNewDeviceOnClick(MouseEvent event) {
        Button button = (Button) event.getSource();
        HBox hBox = hBoxNewSmartphone;
        String brand, model;
        String deviceType = Device.SMARTPHONE;

        if (button.equals(btnConfirmNewDigitalCamera)) {
            hBox = hBoxNewDigitalCamera;
            deviceType = Device.DIGITAL_CAMERA;
        }
        brand = ((TextField) hBox.getChildren().get(0)).getText().trim().replaceAll(" +", " ").toUpperCase();
        model = ((TextField) hBox.getChildren().get(1)).getText().trim().replaceAll(" +", " ").toLowerCase();

        if (recentUserDevices.stream().noneMatch(d -> d.getBrand().equals(brand) && d.getModel().equals(model))) {
            addDeviceTask(brand, model, deviceType, user.getUsername());
            setHboxVisibility(hBox, false);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Device already exists");
            alert.setHeaderText("Device already in your device list");
            alert.setContentText("Change the brand or model of your device!");
            alert.show();
        }
    }

    @FXML
    private void cancelNewDeviceOnClick(MouseEvent event) {
        Button button = (Button) event.getSource();

        if (button.equals(btnCancelNewSmartphone))
            setHboxVisibility(hBoxNewSmartphone, false);

        else if (button.equals(btnCancelNewDigitalCamera))
            setHboxVisibility(hBoxNewDigitalCamera, false);

        btnAddSmartphone.setDisable(false);
        btnAddDigitalCamera.setDisable(false);
    }

    @FXML
    private void addNewDeviceOnClick(MouseEvent event) {
        Button button = (Button) event.getSource();
        HBox hBox = (button.equals(btnAddSmartphone)) ? hBoxNewSmartphone : hBoxNewDigitalCamera;

        setHboxVisibility(hBox, true);
        ((TextField) hBox.getChildren().get(0)).setText("");
        ((TextField) hBox.getChildren().get(1)).setText("");
        btnAddSmartphone.setDisable(true);
        btnAddDigitalCamera.setDisable(true);
    }

    private void getRecentUsedDevicesTask(String username) {
        final Task<ArrayList<Device>> getRecentUserDevicesTask = new Task<>() {
            @Override
            protected ArrayList<Device> call() throws Exception {
                return new DeviceDAOImpl().getRecentUsedDevices(username);
            }
        };
        new Thread(getRecentUserDevicesTask).start();
        getRecentUserDevicesTask.setOnSucceeded(workerStateEvent -> {
            recentUserDevices = getRecentUserDevicesTask.getValue();
            user.setDevices(recentUserDevices);

            if (recentUserDevices != null) {
                setHboxVisibility((HBox) smartphoneVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d -> d.getDeviceType().equals(Device.SMARTPHONE)));
                setHboxVisibility((HBox) digitalCameraVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d -> d.getDeviceType().equals(Device.DIGITAL_CAMERA)));

                for (Device d : recentUserDevices) {
                    if (d.getDeviceType().equals(Device.SMARTPHONE))
                        smartphoneVBox.getChildren().add(createDevicePreview(d.getBrand(), d.getModel(), d.getIdDevice()));
                    else
                        digitalCameraVBox.getChildren().add(createDevicePreview(d.getBrand(), d.getModel(), d.getIdDevice()));
                }
            }
        });
    }

    private void addDeviceTask(String brand, String model, String deviceType, String username) {
        final Task<Device> addDeviceTask = new Task<>() {
            @Override
            protected Device call() throws Exception {
                return new DeviceDAOImpl().addDevice(brand, model, deviceType, username);
            }
        };
        new Thread(addDeviceTask).start();
        addDeviceTask.setOnSucceeded(workerStateEvent -> {
            Device newDevice = addDeviceTask.getValue();
            recentUserDevices.add(newDevice);

            switch (deviceType) {
                case Device.SMARTPHONE -> {
                    smartphoneVBox.getChildren().add(2, createDevicePreview(newDevice.getBrand(), newDevice.getModel(), newDevice.getIdDevice()));
                    setHboxVisibility((HBox) smartphoneVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d -> d.getDeviceType().equals(Device.SMARTPHONE)));
                }
                case Device.DIGITAL_CAMERA -> {
                    digitalCameraVBox.getChildren().add(2, createDevicePreview(newDevice.getBrand(), newDevice.getModel(), newDevice.getIdDevice()));
                    setHboxVisibility((HBox) digitalCameraVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d -> d.getDeviceType().equals(Device.DIGITAL_CAMERA)));
                }
            }

            btnAddSmartphone.setDisable(false);
            btnAddDigitalCamera.setDisable(false);
        });
    }

    private void removeDeviceTask(int idDevice, String username, HBox hBoxRemovedDevice) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Device");
        alert.setHeaderText("Are you sure you want to delete this device?");
        alert.setContentText("This action cannot be undone.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == buttonTypeYes){
            final Task<Void> removeDeviceTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    new DeviceDAOImpl().removeUserDevice(idDevice, username);
                    return null;
                }
            };
            new Thread(removeDeviceTask).start();
            removeDeviceTask.setOnSucceeded(workerStateEvent -> {
                recentUserDevices.removeIf(d -> d.getIdDevice() == idDevice);

                if (smartphoneVBox.getChildren().remove(hBoxRemovedDevice) && smartphoneVBox.getChildren().size() == 2)
                    setHboxVisibility((HBox) smartphoneVBox.getChildren().get(0), true);

                if (digitalCameraVBox.getChildren().remove(hBoxRemovedDevice) && digitalCameraVBox.getChildren().size() == 2)
                    setHboxVisibility((HBox) digitalCameraVBox.getChildren().get(0), true);

                if (device != null && idDevice == device.getIdDevice()) {
                    device = null;
                    lblDeviceSelected.setText("select or add a new Device");
                }
            });
        }
    }

    private void selectDevice(HBox hBoxDeviceSelected) {
        int idDevice = Integer.parseInt(hBoxDeviceSelected.getId());
        device = recentUserDevices.stream().filter(d -> d.getIdDevice() == idDevice).findFirst().orElse(device);

        if (splitPaneDevices.lookup(".selected-device-item") != null)
            splitPaneDevices.lookup(".selected-device-item").getStyleClass().remove(0);
        hBoxDeviceSelected.getStyleClass().add(0, "selected-device-item");

        if (device != null)
            lblDeviceSelected.setText(device.getBrand() + " " + device.getModel());
    }

    private void setHboxVisibility(HBox hBox, boolean visible) {
        hBox.setManaged(visible);
        hBox.setVisible(visible);
    }

    private HBox createDevicePreview(String brand, String model, int id) {
        HBox hBox = new HBox();
        hBox.setId(String.valueOf(id));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().add("device-item");
        hBox.setSpacing(15);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Label brandLabel = new Label(brand);
        brandLabel.setFont(new Font("System Bold", 14));

        Label modelLabel = new Label(model);
        modelLabel.setFont(new Font("System", 12));
        modelLabel.setTextFill(Color.web("#5b5b5b"));

        HBox hBoxRemove = new HBox();
        HBox.setHgrow(hBoxRemove, Priority.ALWAYS);
        hBoxRemove.setAlignment(Pos.CENTER_RIGHT);

        Button btnRemove = new Button("x");
        btnRemove.setFocusTraversable(false);
        btnRemove.getStyleClass().add("xButton");

        btnRemove.setOnAction(actionEvent -> removeDeviceTask(id, user.getUsername(), hBox));
        hBoxRemove.getChildren().add(btnRemove);

        hBox.getChildren().addAll(brandLabel, modelLabel, hBoxRemove);
        hBox.setOnMouseClicked(event -> selectDevice(hBox));

        return hBox;
    }

    public Device getDevice(){
        return this.device;
    }
}
