package com.imagespot;

import com.imagespot.View.ViewFactory;
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

        ViewFactory.getInstance().showAuthWindow();
    }


    public static void main(String[] args) {
        launch();
    }
}
