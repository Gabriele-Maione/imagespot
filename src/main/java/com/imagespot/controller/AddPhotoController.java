package com.imagespot.controller;

import com.imagespot.DAOImpl.DeviceDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.TaggedUserDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Device;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import static com.imagespot.Utils.Utils.*;
import static org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.USER_CHECK;

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
    private TextArea fldDescription;
    @FXML
    private TextField fldSubject;
    @FXML
    private Hyperlink hlinkCancel;
    @FXML
    private ImageView img;
    @FXML
    private Label err;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private TextField searchUser;
    @FXML
    private Label lblDeviceSelected;
    @FXML
    private TilePane taggedUsersList;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ScrollPane scrollPane;
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
    private Button btnConfirmNewSmartphone;
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
    private double x, y;
    private File file;
    private PostDAOImpl postDAO;
    private DeviceDAOImpl deviceDAO;
    private ArrayList<String> taggedUser;
    private User user;
    private Device device;
    private HashMap<String, List<String>> defaultDevices;
    private ArrayList<Device> recentUserDevices;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        device = null;
        progressIndicator.setManaged(false);
        setVisibilityOfHbox(hBoxNewSmartphone, false);
        setVisibilityOfHbox(hBoxNewDigitalCamera, false);

        //Json file
        loadLabels();
        loadDefaultDevices();

        user = ViewFactory.getInstance().getUser();
        cbStatus.getSelectionModel().selectFirst();

        taggedUser = new ArrayList<>();
        taggedUsersListeners();

        getRecentUsedDevicesTask(user.getUsername());
    }

    private void loadLabels(){
        URL lablesJsonFile = getClass().getResource("/json/labels.json");
        JSONObject jsonObject;

        if(lablesJsonFile != null){
            try {
                jsonObject = new JSONObject(IOUtils.toString(lablesJsonFile, StandardCharsets.UTF_8));

                JSONArray jsonCategories = jsonObject.getJSONArray("categories");
                JSONArray jsonStatus = jsonObject.getJSONArray("status");

                for(Object category: jsonCategories)
                    cbCategory.getItems().add(category.toString());


                for(Object state: jsonStatus)
                    cbStatus.getItems().add(state.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            System.out.println("Error opening JSON file!");
    }
    private void loadDefaultDevices(){
        URL devicesJsonFile = getClass().getResource("/json/devices.json");
        JSONObject jsonObject;
        defaultDevices = new HashMap<>();
        defaultDevices.put("Smartphone", new ArrayList<>());
        defaultDevices.put("Digital Camera", new ArrayList<>());

        if(devicesJsonFile != null){
            try {
                jsonObject = new JSONObject(IOUtils.toString(devicesJsonFile, StandardCharsets.UTF_8));

                JSONArray jsonSmartphone = jsonObject.getJSONArray("Smartphone");
                JSONArray jsonDigitalCamera = jsonObject.getJSONArray("Digital Camera");

                for(Object smartphone: jsonSmartphone)
                    defaultDevices.get("Smartphone").add(smartphone.toString());
                for(Object digitalCamera: jsonDigitalCamera)
                    defaultDevices.get("Digital Camera").add(digitalCamera.toString());

                System.out.println("Smartphone: " + defaultDevices.get("Smartphone").toString());
                System.out.println("\n\nDigital Camera: " + defaultDevices.get("Digital Camera").toString());


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            System.out.println("Error opening JSON file!");
    }
    @FXML
    private void confirmNewDeviceOnClick(MouseEvent event){
        Button button = (Button) event.getSource();
        HBox hBox = hBoxNewSmartphone;
        String brand, model;
        String deviceType = "Smartphone";

        if(button.equals(btnConfirmNewDigitalCamera)){
            hBox = hBoxNewDigitalCamera;
            deviceType = "Digital Camera";
        }
        brand = ((TextField)hBox.getChildren().get(0)).getText().trim().replaceAll(" +", " ").toUpperCase();
        model = ((TextField)hBox.getChildren().get(1)).getText().trim().replaceAll(" +", " ").toLowerCase();

        if(recentUserDevices.stream().anyMatch(d -> d.getBrand().equals(brand) && d.getModel().equals(model))){
            System.out.println("Device already in your device list"); //idk where put the error lable lool
        }
        else{
            addDeviceTask(brand, model, deviceType, user.getUsername());
            setVisibilityOfHbox(hBox, false);
        }
    }
    @FXML
    private void cancelNewDeviceOnClick(MouseEvent event){
        Button button = (Button) event.getSource();

        if(button.equals(btnCancelNewSmartphone))
            setVisibilityOfHbox(hBoxNewSmartphone, false);

        else if(button.equals(btnCancelNewDigitalCamera))
            setVisibilityOfHbox(hBoxNewDigitalCamera, false);

        btnAddSmartphone.setDisable(false);
        btnAddDigitalCamera.setDisable(false);
    }
    @FXML
    private void addNewDeviceOnClick(MouseEvent event){
        Button button = (Button) event.getSource();
        HBox hBox = (button.equals(btnAddSmartphone)) ? hBoxNewSmartphone : hBoxNewDigitalCamera;

        setVisibilityOfHbox(hBox, true);
        ((TextField)hBox.getChildren().get(0)).setText("");
        ((TextField)hBox.getChildren().get(1)).setText("");
        btnAddSmartphone.setDisable(true);
        btnAddDigitalCamera.setDisable(true);
    }
    private void setVisibilityOfHbox(HBox hBox, boolean visible){
        hBox.setManaged(visible);
        hBox.setVisible(visible);
    }
    private void taggedUsersListeners() {
        searchUser.setOnMouseClicked(event -> {
            if (contextMenu.getItems().size() == 0) {
                contextMenu.getItems().add(new MenuItem("Search someone"));
            }
            contextMenu.show(searchUser, Side.BOTTOM, 0, 0);
        });
        searchUser.setOnKeyReleased(event -> {
            if (!searchUser.getText().isBlank())
                searchUsersTask(searchUser.getText().trim());
            else {
                contextMenu.getItems().clear();
            }
        });
        searchUser.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                contextMenu.hide();
            }
        });
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
    private void btnPublishOnAction() throws SQLException{
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        deviceDAO = new DeviceDAOImpl();

        if (file == null)
            err.setText("LOAD A PHOTO!!!");
        else if (device == null)
            err.setText("SPECIFY DEVICE!!!");
        else {
            btnPublish.setVisible(false);
            err.setText("");
            final Task<Post> publishPhoto = new Task<>() {
                @Override
                protected Post call() throws Exception {
                    TaggedUserDAOImpl taggedUserDAO = new TaggedUserDAOImpl();
                    Post post = new Post(getRes(file), fldDescription.getText(), getSize(file), getExt(file), timestamp, cbStatus.getValue());

                    post = new PostDAOImpl().addPost(file, post, device, user);
                    post.setProfile(user);

                    int id = post.getIdImage();
                    for (String s : taggedUser)
                        taggedUserDAO.addTag(s, id);

                    return post;
                }
            };
            progressIndicator.setManaged(true);
            progressIndicator.visibleProperty().bind(publishPhoto.runningProperty());
            new Thread(publishPhoto).start();

            publishPhoto.setOnSucceeded(workerStateEvent -> {
                progressIndicator.setManaged(false);
                btnPublish.setVisible(false);
                err.setText("DONE");
                ViewFactory.getInstance().getUser().getPosts().add(0, publishPhoto.getValue());
            });
        }
    }

    private void getRecentUsedDevicesTask(String username){
        final Task<ArrayList<Device>> getRecentUserDevicesTask = new Task<>() {
            @Override
            protected ArrayList<Device> call() throws Exception {
                return new DeviceDAOImpl().getRecentUsedDevices(username);
            }
        };
        new Thread(getRecentUserDevicesTask).start();
        getRecentUserDevicesTask.setOnSucceeded(workerStateEvent -> {
            recentUserDevices = getRecentUserDevicesTask.getValue();

            if(recentUserDevices != null){
                setVisibilityOfHbox((HBox) smartphoneVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d-> d.getDeviceType().equals("Smartphone")));
                setVisibilityOfHbox((HBox) digitalCameraVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d-> d.getDeviceType().equals("Digital Camera")));

                for (Device d : recentUserDevices){
                    if(d.getDeviceType().equals("Smartphone"))
                        smartphoneVBox.getChildren().add(createDevicePreview(d.getBrand(), d.getModel(), d.getIdDevice()));
                    else
                        digitalCameraVBox.getChildren().add(createDevicePreview(d.getBrand(), d.getModel(), d.getIdDevice()));
                }
            }
        });

    }

    private void addDeviceTask(String brand, String model, String deviceType, String username){
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
                case "Smartphone" -> {
                    smartphoneVBox.getChildren().add(2, createDevicePreview(newDevice.getBrand(), newDevice.getModel(), newDevice.getIdDevice()));
                    setVisibilityOfHbox((HBox) smartphoneVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d-> d.getDeviceType().equals("Smartphone")));
                }
                case "Digital Camera" -> {
                    digitalCameraVBox.getChildren().add(2, createDevicePreview(newDevice.getBrand(), newDevice.getModel(), newDevice.getIdDevice()));
                    setVisibilityOfHbox((HBox) digitalCameraVBox.getChildren().get(0), recentUserDevices.stream().noneMatch(d-> d.getDeviceType().equals("Digital Camera")));
                }
            }

            btnAddSmartphone.setDisable(false);
            btnAddDigitalCamera.setDisable(false);
        });
    }

    private void removeDeviceTask(int idDevice, String username, HBox hBoxRemovedDevice){
        final Task<Void> removeDeviceTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                new DeviceDAOImpl().removeUserDevice(idDevice, username);
                return null;
            }
        };
        new Thread(removeDeviceTask).start();
        removeDeviceTask.setOnSucceeded(workerStateEvent -> {
            recentUserDevices.removeIf(d-> d.getIdDevice() == idDevice);

            if(smartphoneVBox.getChildren().remove(hBoxRemovedDevice) && smartphoneVBox.getChildren().size() == 2)
                setVisibilityOfHbox((HBox)smartphoneVBox.getChildren().get(0), true);

            if(digitalCameraVBox.getChildren().remove(hBoxRemovedDevice) && digitalCameraVBox.getChildren().size() == 2)
                setVisibilityOfHbox((HBox)digitalCameraVBox.getChildren().get(0), true);

            if(device != null && idDevice == device.getIdDevice()){
                device = null;
                lblDeviceSelected.setText("select or add a new Device");
            }
        });
    }

    private void searchUsersTask(String searchedString) {
        Task<List<User>> searchedUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                return new UserDAOImpl().findUsers(searchedString);
            }
        };
        new Thread(searchedUsersTask).start();

        searchedUsersTask.setOnSucceeded(workerStateEvent -> {
            if (searchedUsersTask.getValue().size() == 0) {
                MenuItem menuItem = new MenuItem();
                menuItem.setGraphic(new Label("I didn't find anything, try something else"));
                contextMenu.getItems().setAll(menuItem);
            }
            else {
                contextMenu.getItems().clear();
                for (User user : searchedUsersTask.getValue()) {
                    if(!taggedUser.contains(user.getUsername())) {
                        MenuItem menu = new MenuItem();
                        menu.setGraphic(createUserPreview(user.getName(), user.getUsername(), user.getAvatar(), false));
                        menu.setId(user.getUsername());
                        contextMenu.getItems().add(menu);

                        menu.setOnAction(event -> {
                            taggedUser.add(menu.getId());
                            taggedUsersList.getChildren().add(createUserPreview(user.getName(), user.getUsername(), user.getAvatar(), true));
                            contextMenu.getItems().remove(menu);
                        });
                    }
                }
            }
        });
    }

    private HBox createUserPreview(String name, String username, Image pfp, boolean isRemovable) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);
        ImageView avatar = new ImageView();
        avatar.setFitHeight(25);
        avatar.setFitWidth(25);
        avatar.setPickOnBounds(true);
        avatar.setPreserveRatio(true);
        avatar.setImage(Objects.requireNonNullElseGet(pfp, () -> new Image(getClass().getResourceAsStream("/icons/bear_icon.png"))));
        setAvatarRounde(avatar);

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font("System Bold", 14));

        Label usernameLabel = new Label("@" + username);
        usernameLabel.setTextFill(Color.web("#5b5b5b"));

        hbox.getChildren().addAll(avatar, nameLabel, usernameLabel);

        if(ViewFactory.getInstance().getUser().getFollowedUsers()
                .stream().anyMatch(user -> user.getUsername().equals(username))) {
            hbox.getChildren().add(FontIcon.of(USER_CHECK));
        }

        if(isRemovable){
            HBox hBoxRemove = new HBox();
            HBox.setHgrow(hBoxRemove, Priority.ALWAYS);
            hBoxRemove.setAlignment(Pos.CENTER_RIGHT);
            TilePane.setMargin(hbox, new Insets(0, 0, 5, 10));
            Button btnRemove = new Button("x");

            btnRemove.setFocusTraversable(false);
            btnRemove.getStyleClass().add("xButton");

            btnRemove.setOnAction(event -> {
                taggedUser.remove(username);
                taggedUsersList.getChildren().remove(hbox);
            });

            hBoxRemove.getChildren().add(btnRemove);
            hbox.getChildren().add(hBoxRemove);
        }
        return hbox;
    }

    private HBox createDevicePreview(String brand, String model, int id){
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

    private void selectDevice(HBox hBoxDeviceSelected){
        int idDevice = Integer.parseInt(hBoxDeviceSelected.getId());
        device = recentUserDevices.stream().filter(d -> d.getIdDevice() == idDevice).findFirst().orElse(device);

        if(splitPaneDevices.lookup(".selected-device-item") != null)
            splitPaneDevices.lookup(".selected-device-item").getStyleClass().remove(0);
        hBoxDeviceSelected.getStyleClass().add(0, "selected-device-item");

        if(device != null)
            lblDeviceSelected.setText(device.getBrand() + " " + device.getModel());
    }

    @FXML
    private void closeButtonOnAction() {
        Stage stage = (Stage) btnPublish.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void dragged(MouseEvent ev) {
        Stage stage = (Stage) btnPublish.getScene().getWindow();
        stage.setX(ev.getScreenX() - x);
        stage.setY(ev.getScreenY() - y);
    }

    @FXML
    private void pressed(MouseEvent ev) {
        x = ev.getSceneX();
        y = ev.getSceneY();
    }
}
