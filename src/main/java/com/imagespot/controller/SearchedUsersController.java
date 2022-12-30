package com.imagespot.controller;

import com.imagespot.DAOImpl.PostDAOImpl;
import com.imagespot.DAOImpl.UserDAOImpl;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;



public class SearchedUsersController implements Initializable {

    @FXML
    private FlowPane flowPane;
    private String searchedString;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(String string) {
        searchedString = string;

        try {
            displaySearchedUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void displaySearchedUsers() throws SQLException {

        System.out.println(searchedString);
        List<User> users = new UserDAOImpl().findUsers(searchedString);

        if(users.size() == 0) flowPane.getChildren().add((new Label("I didn't found anything, try something else")));
        for (User user : users) {
            System.out.println(user.getUsername());
            HBox postBox = ViewFactory.getInstance().getUserPreview(user);

            flowPane.getChildren().add(postBox);
        }
    }
}
