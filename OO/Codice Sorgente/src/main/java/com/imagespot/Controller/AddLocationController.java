package com.imagespot.Controller;

import com.imagespot.Model.Location;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import org.json.JSONObject;
import java.net.URL;
import java.util.ResourceBundle;

public class AddLocationController implements Initializable {
    @FXML
    private HBox hbLoc;
    @FXML
    private Label addressLbl;
    @FXML
    private WebView webView;
    private Location location;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hbLoc.setManaged(false);
        hbLoc.setVisible(false);
        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().setUserAgent("foo\nAuthorization: Basic " + java.util.Base64.getEncoder().encodeToString(("imagespot" + ":" + "forzanapoli!").getBytes()));
        webView.getEngine().load("https://myapplications.altervista.org/imagespot/location/location.php");
        webView.getEngine().setOnAlert(stringWebEvent -> {
            setLocationFromMap(stringWebEvent.getData());
        });
    }

    private void setLocationFromMap(String json) {
        hbLoc.setManaged(true);
        hbLoc.setVisible(true);

        JSONObject jsonResponse = new JSONObject(json);

        if (jsonResponse.has("original") && jsonResponse.getJSONObject("original").has("details")) {
            location = new Location();
            JSONObject details = jsonResponse.getJSONObject("original").getJSONObject("details");

            location.setFormatted_address(jsonResponse.getJSONObject("original").getString("formatted"));
            location.setLatitude(jsonResponse.getBigDecimal("lat"));
            location.setLongitude(jsonResponse.getBigDecimal("lon"));

            if (details.has("country"))
                location.setCountry(details.getString("country"));
            if (details.has("state"))
                location.setState(details.getString("state"));
            else if (details.has("region"))
                location.setState(details.getString("region"));
            if (details.has("city"))
                location.setCity(details.getString("city"));
            else if (details.has("town"))
                location.setCity(details.getString("town"));
            else if (details.has("village"))
                location.setCity(details.getString("village"));
            if (details.has("postcode"))
                location.setPostcode(details.getString("postcode"));
            if (details.has("road"))
                location.setRoad(details.getString("road"));

            addressLbl.setText(location.toString());
        } else {
            location = null;
            addressLbl.setText("No results found");
        }
    }

    @FXML
    private void btnRemoveLocationOnAction() {
        location = null;
        addressLbl.setText("");
        hbLoc.setManaged(false);
        hbLoc.setVisible(false);
        webView.getEngine().executeScript("clear()");
    }

    public Location getLocation() {
        return location;
    }
}
