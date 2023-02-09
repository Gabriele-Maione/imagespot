package com.imagespot.model;

import java.util.ArrayList;

public class Subject {
    private Integer id;
    private String category;
    private String subject;
    private int imageID;

    public Subject(int id, String category, String subject) {
        this.id = id;
        this.category = category;
        this.subject = subject;
    }

    public Subject(String category, String subject) {
        this.category = category;
        this.subject = subject;
    }

    public Subject() {}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

}
