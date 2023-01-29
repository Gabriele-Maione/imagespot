package com.imagespot.controller;

import com.imagespot.DAOImpl.LocationDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopPlacesController implements Initializable {

    @FXML
    private HBox hbCountries;
    @FXML
    private HBox hbPlaces;
    @FXML
    private HBox hbCities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTopPlaces();
    }

    private void loadTopPlaces() {
        LocationDAOImpl locDAO = new LocationDAOImpl();
        List<String> countries = locDAO.getTop("country");
        List<String> cities = locDAO.getTop("city");
        List<String> places = locDAO.getTop("formatted_address");
        for (String country : countries) {
            hbCountries.getChildren().addAll(getContainer(new PostDAOImpl()
                    .getPreviewForLocation(country, "country"), country, "country"));
        }
        for (String city : cities) {
            hbCities.getChildren().addAll(getContainer(new PostDAOImpl()
                    .getPreviewForLocation(city, "city"), city, "city"));
        }
        for (String place : places) {
            hbPlaces.getChildren().addAll(getContainer(new PostDAOImpl()
                    .getPreviewForLocation(place, "formatted_address"), place, "formatted_address"));
        }
    }

    private StackPane getContainer(Image img, String location, String type) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(150);
        stackPane.setPrefWidth(150);

        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        Color darkOpacity = Color.color(0, 0, 0, 0.5);
        Rectangle rect = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight(), darkOpacity);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);

        Label label = new Label(location);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);
        label.setFont(new Font("System Bold", 18));

        stackPane.getChildren().addAll(imageView, rect, label);
        stackPane.setOnMouseClicked(mouseEvent -> {
            BorderPane borderPane = (BorderPane) stackPane.getScene().getRoot();
            borderPane.setCenter(ViewFactory.getInstance().getPostsByPlace(location, type));
        });
        return stackPane;
    }
}
