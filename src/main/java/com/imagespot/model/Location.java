package com.imagespot.model;

import java.util.ArrayList;
import java.math.BigDecimal;


public class Location {
    private Integer idLocation;
    private String country;
    private String state;
    private String city;
    private String postacode;
    private String formatted_address;
    private String road;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private ArrayList<Post> posts;

    public Location(int id) {
        this.idLocation = id;
    }
    public Location() {}

    public Integer getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Integer idLocation) {
        this.idLocation = idLocation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostacode() {
        return postacode;
    }

    public void setPostacode(String postacode) {
        this.postacode = postacode;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
