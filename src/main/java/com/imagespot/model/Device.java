package com.imagespot.model;

import java.util.ArrayList;

public class Device {
    private Integer idDevice;
    private String brand;
    private String model;
    private String deviceType;
    private ArrayList<Post> posts;
    public static final String SMARTPHONE = "Smartphone";
    public static final String DIGITAL_CAMERA = "Digital Camera";
    public Device(){}

    public Device(int idDevice, String brand, String model, String deviceType){
        this.idDevice = idDevice;
        this.brand = brand;
        this.model = model;
        this.deviceType = deviceType;
    }

    public Integer getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(Integer idDevice) {
        this.idDevice = idDevice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

}
