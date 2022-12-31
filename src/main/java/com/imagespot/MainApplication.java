package com.imagespot;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.Utils.MapUtils;
import com.imagespot.View.ViewFactory;
import com.imagespot.controller.SignInController;
import com.imagespot.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.text.View;
import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {

        ViewFactory.getInstance().showAuthWindow();
    }


    public static void main(String[] args) {
        launch();
    }
}
