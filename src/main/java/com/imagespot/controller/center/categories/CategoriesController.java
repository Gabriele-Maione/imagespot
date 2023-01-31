package com.imagespot.controller.center.categories;

import com.imagespot.View.ViewFactory;
import com.imagespot.controller.center.CenterPaneController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class CategoriesController extends CenterPaneController {

    @FXML
    private Button btnUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setVisible(false);
        loadPosts();
    }

    @Override
    protected void loadPosts() {
        URL lablesJsonFile = getClass().getResource("/json/labels.json");
        JSONObject jsonObject;
        System.out.println("BIBAAAAAA");

        if (lablesJsonFile != null) {
            try {
                jsonObject = new JSONObject(IOUtils.toString(lablesJsonFile, StandardCharsets.UTF_8));

                JSONArray jsonCategories = jsonObject.getJSONArray("categories");

                for (Object category : jsonCategories)
                    flowPane.getChildren().add(getContainer(category.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            System.out.println("Error opening JSON file!");
    }

    private StackPane getContainer(String category) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(150);
        stackPane.setPrefWidth(150);

        Color darkOpacity = Color.color(0, 0, 0, 0.5);
        Rectangle rect = new Rectangle(150, 150, darkOpacity);

        Label label = new Label(category);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        label.setFont(new Font("System Bold", 18));

        stackPane.getChildren().addAll(rect, label);
        stackPane.setOnMouseClicked(mouseEvent -> {
            BorderPane borderPane = (BorderPane) stackPane.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getPostsByCategory(category));
        });
        return stackPane;
    }
}
