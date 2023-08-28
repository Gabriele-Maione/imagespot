package com.imagespot.model;

import java.math.BigDecimal;


public class Location {
    private Integer idLocation;
    private String country;
    private String state;
    private String city;
    private String postcode;
    private String formatted_address;
    private String road;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Post post;

    public Location(int id) {
        this.idLocation = id;
    }
    public Location() {}

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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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

    public Post getPost() { return post; }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(country != null)
            s.append("Country\t\t").append(country);
        if(state != null)
            s.append("\nState\t\t").append(state);
        if(city != null)
            s.append("\nCity\t\t\t").append(city);
        if(postcode != null)
            s.append("\nPostcode\t\t").append(postcode);
        if(road != null)
            s.append("\nRoad\t\t").append(road);
        s.append("\nCoord\t\t").append(latitude).append(", ").append(longitude);
        s.append("\nFormatted\t").append(formatted_address);

        return s.toString();
    }
}
