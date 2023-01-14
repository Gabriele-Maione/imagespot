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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
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
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private TextField searchUser;
    @FXML
    private TilePane taggedUsersList;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ScrollPane scrollPane;

    private File file;

    private PostDAOImpl postDAO;
    private DeviceDAOImpl deviceDAO;

    private ArrayList<String> taggedUser;
    private User user;
    private Device device;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.setManaged(false);

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
        cbType.setValue("Smartphone");
        fldBrand.setText("Xiaomi");
        fldModel.setText("Mi8");

        taggedUser = new ArrayList<>();
        taggedUsersListeners();
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
    private void btnPublishOnAction() throws SQLException, IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        device = new Device();
        deviceDAO = new DeviceDAOImpl();

        if (file == null)
            err.setText("LOAD A PHOTO!!!");
        else if (fldBrand.getText().isBlank() || fldModel.getText().isBlank() || cbType.getValue() == null)
            err.setText("SPECIFY DEVICE!!!");
        else {
            btnPublish.setVisible(false);
            device.setIdDevice(deviceDAO.addDevice(fldBrand.getText(), fldModel.getText(), cbType.getValue()));
            device.setBrand(fldBrand.getText());
            device.setModel(fldModel.getText());
            device.setDeviceType(cbType.getValue());
            final Task<Post> publishPhoto = new Task<>() {
                @Override
                protected Post call() throws Exception {
                    TaggedUserDAOImpl taggedUserDAO = new TaggedUserDAOImpl();
                    Post post = new Post(getRes(file), fldDescription.getText(), getSize(file), getExt(file), timestamp, cbStatus.getValue());

                    post = new PostDAOImpl().addPost(file, post, device, user);
                    post.setProfile(user);

                    int id = post.getIdImage();
                    for (String s : taggedUser) {
                        taggedUserDAO.addTag(s,id);
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
}
