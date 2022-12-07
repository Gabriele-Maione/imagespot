package com.imagespot.model;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Post {
    private Integer idImage;
    private File photo;
    private String resolution;
    private String description;
    private int size;
    private String extension;
    private Timestamp date;
    private String status;
    private Device device;
    private User profile;
    private Location location;
    private ArrayList<User> taggedUsers;
    private ArrayList<Reaction> reactions;

    public ArrayList<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(ArrayList<Reaction> reactions) {
        this.reactions = reactions;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public ArrayList<User> getTaggedUsers() {
        return taggedUsers;
    }

    public void setTaggedUsers(ArrayList<User> taggedUsers) {
        this.taggedUsers = taggedUsers;
    }

    public Integer getIdImage() {
        return idImage;
    }

    public void setIdImage(Integer idImage) {
        this.idImage = idImage;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getProfile() {
        return profile;
    }

    public void setProfile(User profile) {
        this.profile = profile;
    }
}

