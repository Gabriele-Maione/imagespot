package com.imagespot.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;

public class User {
    private String username;
    private ObjectProperty<String> name;
    private String email;
    private String password;
    private String gender;
    private String bio;
    private ObjectProperty<Image> avatar;
    private ArrayList<User> following;
    private ArrayList<Post> posts;
    private ArrayList<Reaction> reaction;

    public User() {
        this.avatar = new SimpleObjectProperty<Image>();
        this.name = new SimpleObjectProperty<String>();
    }

    public ArrayList<User> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Reaction> getReaction() {
        return reaction;
    }

    public void setReaction(ArrayList<Reaction> reaction) {
        this.reaction = reaction;
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
