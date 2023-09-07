package com.imagespot.Controller;

import com.imagespot.ImplementationPostgresDAO.*;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.*;
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
import java.util.*;
import static com.imagespot.Utils.Utils.*;
import static org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.USER_CHECK;

public class AddPostController implements Initializable {
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
    private TilePane taggedUsersList;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TilePane categoryTilePane;
    @FXML
    private Label subErr;
    private double x, y;
    private File file;
    private ArrayList<String> taggedUser;
    private User user;
    private Device device;
    private Location location;
    private ArrayList<Subject> subjects;
    @FXML
    private DeviceController deviceViewController;
    @FXML
    private AddLocationController locationViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.setManaged(false);

        //Json file
        loadLabels();

        user = ViewFactory.getInstance().getUser();
        cbStatus.getSelectionModel().selectFirst();

        taggedUser = new ArrayList<>();
        taggedUsersListeners();

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
        }
    }

    private void taggedUsersListeners() {
        searchUser.setOnMouseClicked(event -> {
            if (contextMenu.getItems().size() == 0) {
                contextMenu.getItems().add(new MenuItem("Search someone"));
            }
            contextMenu.show(searchUser, Side.BOTTOM, 0, 0);
        });
        searchUser.setOnKeyReleased(event -> {
            contextMenu.show(searchUser, Side.BOTTOM, 0, 0);
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
        device = deviceViewController.getDevice();
        location = locationViewController.getLocation();

        if (file == null)
            err.setText("Please, LOAD A PHOTO!!!");
        else if (device == null)
            err.setText("Please, SPECIFY A DEVICE!!!");
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

                    JSONObject response = new JSONObject(uploadFile(file, photoScaler2(file)));

                    if(response.getBoolean("success")){
                        new PostDAOImpl().addPost(response.getString("path"), response.getString("preview_path"), post, device, user);
                    }

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
                ViewFactory.getInstance().getUser().addPost(publishPhoto.getValue());
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
