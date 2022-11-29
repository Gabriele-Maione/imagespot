package com.imagespot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {
    public Button btn_sign_in;
    @FXML
    private Label lbl;

    @FXML
    protected void onSignInClick() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Home");
            stage.setScene(scene);
            btn_sign_in.getScene().getWindow().hide();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}