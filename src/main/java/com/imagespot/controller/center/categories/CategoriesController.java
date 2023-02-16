package com.imagespot.controller.center.categories;

import com.imagespot.View.ViewFactory;
import com.imagespot.controller.center.CenterPaneController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriesController extends CenterPaneController {

    @FXML
    private Button btnUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setVisible(false);
        loadPosts();
        progressIndicator.setVisible(false);
        btnUpdate.setVisible(false);
        name.setText("Categories");

        flowPaneResponsive(flowPane);
        flowPane.setVgap(5);
        flowPane.setHgap(5);
    }

    @Override
    protected void loadPosts() {
        URL lablesJsonFile = getClass().getResource("/json/labels.json");
        JSONObject jsonObject;

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

    @Override
    protected void setFlowPaneChildWidth(List<Node> flowPaneChild, double flowPaneWidth) {
        for (Node box : flowPaneChild) {
            if(box instanceof StackPane v){
                int numNodeForRow = (int) (flowPaneWidth / 178);
                double nodeWidth = flowPaneWidth / numNodeForRow;

                v.setPrefWidth(nodeWidth - 6);
                v.setPrefHeight(nodeWidth - 6);
            }
        }
    }

    private StackPane getContainer(String category) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(150);
        stackPane.setPrefWidth(150);
        stackPane.getStyleClass().add("categoryBox");
        stackPane.setStyle("-fx-background-color: linear-gradient(to bottom, "
                + "rgba(" + (int)(Math.random() * 200 + 55) + "," + (int)(Math.random() * 200 + 55) + "," + (int)(Math.random() * 200 + 55) + ",1), "
                + "rgba(" + (int)(Math.random() * 200 + 55) + "," + (int)(Math.random() * 200 + 55) + "," + (int)(Math.random() * 200 + 55) + ",1));");
        Label label = new Label(category);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        label.setFont(new Font("System Bold", 18));

        stackPane.getChildren().addAll(label);
        stackPane.setOnMouseClicked(mouseEvent -> {
            BorderPane borderPane = (BorderPane) stackPane.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getPostsByCategory(category));
        });
        return stackPane;
    }
}
