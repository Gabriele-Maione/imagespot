package com.imagespot.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import java.util.ArrayList;


public class User {
    private String username;
    private ObjectProperty<String> name;
    private String email;
    private String password;
    private String gender;
    private String bio;
    private ObjectProperty<Image> avatar;
    private ObservableList<User> followedUsers;
    private ObservableList<Post> posts;
    private ArrayList<Post> bookmark;

    public User() {
        this.avatar = new SimpleObjectProperty<>();
        this.name = new SimpleObjectProperty<>();
        this.followedUsers = FXCollections.observableList(new ArrayList<>());
        this.posts = FXCollections.observableList(new ArrayList<>());
    }

    public ObservableList<User> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(ObservableList<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

    public ObservableList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ObservableList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Post> getBookmarks() {
        return bookmark;
    }

    public void setBookmarks(ArrayList<Post> bookmark) {
        this.bookmark = bookmark;
    }

    public ObjectProperty<Image> avatarProperty() { return avatar; }

    public Image getAvatar() {
        return avatar.getValue();
    }

    public void setAvatar(Image avatar) {
        this.avatar.setValue(avatar);
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectProperty<String> nameProperty() { return name; }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}
