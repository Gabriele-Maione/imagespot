package com.imagespot.controller;

import com.imagespot.DAOImpl.*;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.*;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    @FXML
    private TilePane categoryTilePane;
    @FXML
    private Label subErr;
    @FXML
    private HBox hbLoc;

    //Address
    private static final String API_KEY = "59fe0bec7ad34ae9a0ee9ee9e38c2d3d";
    @FXML
    private Label addressLbl;
    @FXML
    private TextField coordinatesFld;
    @FXML
    private ContextMenu locationCm;

    //--------------------------------------//
    private double x, y;
    private File file;
    private ArrayList<String> taggedUser;
    private User user;
    private Device device;
    private ArrayList<Device> recentUserDevices;
    private Location location;
    private ArrayList<Subject> subjects;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        device = null;
        progressIndicator.setManaged(false);
        setHboxVisibility(hBoxNewSmartphone, false);
        setHboxVisibility(hBoxNewDigitalCamera, false);

        //Json file
        loadLabels();

        user = ViewFactory.getInstance().getUser();
        cbStatus.getSelectionModel().selectFirst();

        taggedUser = new ArrayList<>();
        taggedUsersListeners();

        getRecentUsedDevicesTask(user.getUsername());

        hbLoc.setManaged(false);
        hbLoc.setVisible(false);
        locationListener();

        subjects = new ArrayList<>();
    }

    private void loadLabels() {
        URL lablesJsonFile = getClass().getResource("/json/labels.json");
        JSONObject jsonObject;

        if (lablesJsonFile != null) {
            try {
                jsonObject = new JSONObject(IOUtils.toString(lablesJsonFile, StandardCharsets.UTF_8));

                JSONArray jsonCategories = jsonObject.getJSONArray("categories");
                JSONArray jsonStatus = jsonObject.getJSONArray("status");

                for (Object category : jsonCategories)
                    cbCategory.getItems().add(category.toString());


                for (Object state : jsonStatus)
                    cbStatus.getItems().add(state.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            System.out.println("Error opening JSON file!");
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

        if (recentUserDevices.stream().anyMatch(d -> d.getBrand().equals(brand) && d.getModel().equals(model))) {
            System.out.println("Device already in your device list");
        } else {
            addDeviceTask(brand, model, deviceType, user.getUsername());
            setHboxVisibility(hBox, false);
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

    private void setHboxVisibility(HBox hBox, boolean visible) {
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
        if (file != null) {
            img.setImage(new Image((file.getAbsolutePath())));
            btnUpload.setText("Change");
        }
    }

    @FXML
    private void btnPublishOnAction() {
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
                    SubjectDAOImpl subjectDAO = new SubjectDAOImpl();
                    String description = fldDescription.getText().isBlank() ? null : fldDescription.getText();

                    Post post = new Post(getRes(file), description, getSize(file), getExt(file), cbStatus.getValue(), location);

                    new PostDAOImpl().addPost(file, post, device, user);
                    post.setProfile(user);

                    if (location != null) {
                        location.setPost(post);
                        new LocationDAOImpl().addLocation(location);
                    }

                    for (String s : taggedUser)
                        taggedUserDAO.addTag(s, post.getIdImage());
                    for (Subject s : subjects) {
                        s.setImageID(post.getIdImage());
                        subjectDAO.addSubject(s);
                    }

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
            } else {
                contextMenu.getItems().clear();
                for (User user : searchedUsersTask.getValue()) {
                    if (!taggedUser.contains(user.getUsername())) {
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
        avatar.setImage(Objects.requireNonNullElseGet(pfp, () -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/bear_icon.png")))));
        setAvatarRounde(avatar);

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font("System Bold", 14));

        Label usernameLabel = new Label("@" + username);
        usernameLabel.setTextFill(Color.web("#5b5b5b"));

        hbox.getChildren().addAll(avatar, nameLabel, usernameLabel);

        if (ViewFactory.getInstance().getUser().getFollowedUsers()
                .stream().anyMatch(user -> user.getUsername().equals(username))) {
            hbox.getChildren().add(FontIcon.of(USER_CHECK));
        }

        if (isRemovable) {
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

    private void selectDevice(HBox hBoxDeviceSelected) {
        int idDevice = Integer.parseInt(hBoxDeviceSelected.getId());
        device = recentUserDevices.stream().filter(d -> d.getIdDevice() == idDevice).findFirst().orElse(device);

        if (splitPaneDevices.lookup(".selected-device-item") != null)
            splitPaneDevices.lookup(".selected-device-item").getStyleClass().remove(0);
        hBoxDeviceSelected.getStyleClass().add(0, "selected-device-item");

        if (device != null)
            lblDeviceSelected.setText(device.getBrand() + " " + device.getModel());
    }

    //Address stuff

    private void locationListener() {
        coordinatesFld.setOnMouseClicked(mouseEvent -> {
            if (locationCm.getItems().isEmpty()) {
                locationCm.getItems().add(new MenuItem("Insert address or coordinates"));
            }
            locationCm.show(coordinatesFld, Side.BOTTOM, 0, 0);
        });

        coordinatesFld.setOnKeyReleased(event -> {
            if (coordinatesFld.getText().length() > 5) {
                Task<JSONObject> addressReq = searchAddressesTask(coordinatesFld.getText());
                new Thread(addressReq).start();

                addressReq.setOnSucceeded(workerStateEvent -> {
                    locationCm.getItems().clear();
                    JSONObject json = addressReq.getValue();
                    JSONArray results = json.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        String formattedAddress = results.getJSONObject(i).getString("formatted");
                        MenuItem menuItem = new MenuItem(formattedAddress);
                        menuItem.setOnAction(event1 -> {
                            locationCm.hide();
                            coordinatesFld.setText(formattedAddress);
                        });
                        locationCm.getItems().add(menuItem);
                    }
                    locationCm.show(coordinatesFld, Side.BOTTOM, 0, 0);
                });
            }
        });

        coordinatesFld.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                locationCm.hide();
            }
        });
    }

    private Task<JSONObject> searchAddressesTask(String searchText) {
        return new Task<>() {
            @Override
            protected JSONObject call() {
                try {
                    String text = searchText.replace(" ", "+");
                    URL url = new URL("https://api.opencagedata.com/geocode/v1/json?limit=5&language=en&q=" + text + "&key=" + API_KEY);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    String response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
                    return new JSONObject(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @FXML
    private void btnFindCoordinatesOnAction() {
        if (coordinatesFld.getText().length() > 0) {
            String coordinatesFldText = coordinatesFld.getText();
            Task<JSONObject> addressReq = searchAddressesTask(coordinatesFldText);
            new Thread(addressReq).start();

            addressReq.setOnSucceeded(workerStateEvent -> {
                JSONObject jsonResponse = addressReq.getValue();
                hbLoc.setManaged(true);
                hbLoc.setVisible(true);
                if (jsonResponse.getJSONArray("results").length() > 0) {
                    location = new Location();
                    JSONArray results = jsonResponse.getJSONArray("results");
                    JSONObject component = results.getJSONObject(0).getJSONObject("components");
                    String address = "";
                    location.setFormatted_address(results.getJSONObject(0).getString("formatted"));
                    location.setLatitude(results.getJSONObject(0).getJSONObject("geometry").getBigDecimal("lat"));
                    location.setLongitude(results.getJSONObject(0).getJSONObject("geometry").getBigDecimal("lng"));
                    if (component.has("country")) {
                        address += "Country\t\t" + component.getString("country") + "\n";
                        location.setCountry(component.getString("country"));
                    }
                    if (component.has("state")) {
                        address += "State\t\t" + component.getString("state") + "\n";
                        location.setState(component.getString("state"));
                    } else if (component.has("region")) {
                        address += "State\t\t" + component.getString("region") + "\n";
                        location.setState(component.getString("region"));
                    }
                    if (component.has("city")) {
                        address += "City\t\t\t" + component.getString("city") + "\n";
                        location.setCity(component.getString("city"));
                    } else if (component.has("town")) {
                        address += "City\t\t\t" + component.getString("town") + "\n";
                        location.setCity(component.getString("town"));
                    } else if (component.has("village")) {
                        address += "City\t\t\t" + component.getString("village") + "\n";
                        location.setCity(component.getString("village"));
                    }
                    if (component.has("postcode")) {
                        address += "Postcode\t\t" + component.getString("postcode") + "\n";
                        location.setPostcode(component.getString("postcode"));
                    }
                    if (component.has("road")) {
                        address += "Road\t\t" + component.getString("road") + "\n";
                        location.setRoad(component.getString("road"));
                    }
                    address += "Coord\t\t" + location.getLatitude() + ", " + location.getLongitude() + "\n";
                    address += "Formatted\t" + location.getFormatted_address();
                    addressLbl.setText(address);
                } else {
                    location = null;
                    addressLbl.setText("No results found");
                }
            });
        }
    }

    @FXML
    private void btnRemoveLocationOnAction() {
        location = null;
        addressLbl.setText("");
        hbLoc.setManaged(false);
        hbLoc.setVisible(false);
    }

    //  CATEGORIES/SUBJECTS STUFF
    @FXML
    private void addSubjectOnAction() {
        if (cbCategory.getValue() != null && !fldSubject.getText().isEmpty()) {
            if(subjects.size() < 5){
                    if(subjects.stream().noneMatch(s -> s.getSubject().equals(fldSubject.getText()))) {
                        subErr.setText("");
                        Subject newSubject = new Subject(cbCategory.getValue(), fldSubject.getText());
                        subjects.add(newSubject);
                        categoryHBox(newSubject);
                    }
                    else
                        subErr.setText("The subject has already been added");
            }
            else subErr.setText("Max 5 subjects");
        }
        else {
            subErr.setText("Category or subject should not be empty");
        }
    }

    private void categoryHBox(Subject newSubject) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        TilePane.setMargin(hbox, new Insets(0, 0, 5, 10));

        VBox vbox = new VBox();

        Label categoryLabel = new Label(newSubject.getCategory());
        categoryLabel.setFont(new Font("System Bold", 14));

        Label subjectLabel = new Label(newSubject.getSubject());
        subjectLabel.setFont(new Font(14));

        vbox.getChildren().addAll(categoryLabel, subjectLabel);

        Button removeButton = new Button("x");
        removeButton.getStyleClass().add("xButton");
        removeButton.setMnemonicParsing(false);
        removeButton.setOnAction(event -> {
            subjects.remove(newSubject);
            categoryTilePane.getChildren().remove(hbox);
        });

        hbox.getChildren().addAll(vbox, removeButton);
        categoryTilePane.getChildren().addAll(hbox);
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
