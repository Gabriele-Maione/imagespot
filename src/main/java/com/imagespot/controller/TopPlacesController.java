package com.imagespot.controller;

import com.imagespot.DAOImpl.LocationDAOImpl;
import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.View.ViewFactory;
import javafx.concurrent.Task;
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
    @FXML
    protected ProgressIndicator progressIndicator;

    private List<String> countries;
    private List<String> cities;
    private List<String> places;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getTopTask();
    }

    private void getTopTask() {
        final Task<Void> getTop = new Task<>() {
            @Override
            protected Void call() {
                LocationDAOImpl locDAO = new LocationDAOImpl();
                countries = locDAO.getTop("country");
                cities = locDAO.getTop("city");
                places = locDAO.getTop("formatted_address");
                return null;
            }
        };
        new Thread(getTop).start();
        getTop.setOnSucceeded(workerStateEvent -> loadTopPlaces());
    }

    private void loadTopPlaces() {

        for (String country : countries)
            hbCountries.getChildren().add(getContainer(country, "country"));

        for (String city : cities)
            hbCities.getChildren().add(getContainer(city, "city"));

        for (String place : places)
            hbPlaces.getChildren().add(getContainer(place, "formatted_address"));
    }

    private void getPreviewTask(ImageView imageView, String location, String type) {
        final Task<Image> getPreview = new Task<>() {
            @Override
            protected Image call() {
                return new PostDAOImpl().getPreviewForLocation(location, type);
            }
        };
        progressIndicator.visibleProperty().bind(getPreview.runningProperty());
        new Thread(getPreview).start();
        getPreview.setOnSucceeded(workerStateEvent -> imageView.setImage(getPreview.getValue()));
    }

    @FXML
    protected void btnUpdateOnAction() {
        hbCities.getChildren().clear();
        hbCountries.getChildren().clear();
        hbPlaces.getChildren().clear();
        getTopTask();
    }

    private StackPane getContainer(String location, String type) {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("categoryBox");
        stackPane.setPrefHeight(200);
        stackPane.setPrefWidth(200);

        ImageView imageView = new ImageView();
        getPreviewTask(imageView, location, type);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
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
