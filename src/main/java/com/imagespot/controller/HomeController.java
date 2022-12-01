package com.imagespot.controller;

import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    private User user;

    @FXML
    private Label label_home;

    public void setUser(User u) {
        this.user = u;
        if (user != null) {
            label_home.setText(u.getName());
        }
    }
}
