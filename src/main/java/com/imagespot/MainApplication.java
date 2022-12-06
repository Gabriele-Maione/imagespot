package com.imagespot;

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
    /*public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sign-in-view.fxml"));
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root, 300, 400);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }*/

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sign-in-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 550);
        stage.setTitle("Imagespot");
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
