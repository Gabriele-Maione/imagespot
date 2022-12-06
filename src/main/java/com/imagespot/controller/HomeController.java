package com.imagespot.controller;

import com.imagespot.MainApplication;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private GridPane postGrid;
    private List<Post> posts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //posts = new ArrayList<>(data());

        Post post = new Post();

        for(int i=0; i<9; i++){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation((MainApplication.class.getResource("images-preview.fxml")));

                VBox postBox = fxmlLoader.load();

                ImagesController imagesController = fxmlLoader.getController();
                //imagesController.setData(post);

                postGrid.add(postBox, i%3, i/3 + 1);
                GridPane.setMargin(postBox, new Insets(10));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

     private  List<Post> data() {
        List<Post> listpost = new ArrayList<>();

        Post post = new Post();
        listpost.add(post);

        return listpost;
     }
}
