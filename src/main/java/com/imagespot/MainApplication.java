package com.imagespot;

import com.imagespot.controller.SignInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Scene scene;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/imagespot/sign-in-view.fxml"));

            stage.setTitle("Login");
            scene = new Scene(fxmlLoader.load(), 480, 600);
            stage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch(IOException e) { e.printStackTrace(); }
    }


    public static void main(String[] args) {
        launch();
    }
}
