package com.imagespot.model;

import java.util.ArrayList;

public class Collection {
    private Integer idCollection;

    private String name;

    private String description;

    private User owner;

    private ArrayList<Post> posts;

    private int postsSize;

    private int memberSize;

    public Collection(){};

    public Collection(Integer idCollection, String name, String description, User owner){
        this.idCollection = idCollection;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.posts = new ArrayList<>();
        this.postsSize = 0;
    }

    public Integer getIdCollection() {
        return idCollection;
    }

    public void setIdCollection(Integer idCollection) {
        this.idCollection = idCollection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public int getPostsSize() {
        return postsSize;
    }

    public void setPostsSize(int postsSize) {
        this.postsSize = postsSize;
    }

    public int getMemberSize() {
        return memberSize;
    }

    public void setMemberSize(int memberSize) {
        this.memberSize = memberSize;
    }
}
