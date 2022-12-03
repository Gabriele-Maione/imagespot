package com.imagespot.model;
import java.util.ArrayList;
import java.math.BigDecimal;


public class Location {
    private Integer idLocation;
    private String country;
    private String region;
    private String city;
    private String postacode;
    private String metrocode;
    private String areacode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private ArrayList<Post> posts;

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getMetrocode() {
        return metrocode;
    }

    public void setMetrocode(String metrocode) {
        this.metrocode = metrocode;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
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
