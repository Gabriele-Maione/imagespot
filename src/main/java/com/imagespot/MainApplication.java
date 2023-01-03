package com.imagespot;

import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {

        //new UserDAOImpl().getUserInfo("Fel");
        ViewFactory.getInstance().showAuthWindow();
    }


    public static void main(String[] args) {
        launch();
    }
}
