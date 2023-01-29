package com.imagespot.controller;

import com.imagespot.DAOImpl.LocationDAOImpl;
import com.imagespot.View.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopPlacesController implements Initializable {


    @FXML
    private Button btnUpdate;

    @FXML
    private FlowPane flopaneCountries;

    @FXML
    private FlowPane flowPane;

    @FXML
    private FlowPane flowPane2;

    @FXML
    private Label flowPanePlaces;

    @FXML
    private VBox flowpaneCities;

    @FXML
    private Label name;

    @FXML
    private Label name11;

    @FXML
    private Label name12;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ScrollPane scrollPane;

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
            System.out.println(country);
            Label label = new Label(country);
            FlowPane.setMargin(label, new Insets(5,5,5,5));
            flopaneCountries.getChildren().addAll(label);
        }

    }
}
