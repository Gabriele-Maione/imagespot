package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.Location;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public interface PostDAO {
    void addPost(File photo, Post post, Device device, User profile) throws SQLException, IOException;
    ArrayList<Post> getRecentPosts(Timestamp timestamp) throws SQLException;
    ArrayList<Post>getUserPosts(String username, Timestamp timestamp) throws SQLException;
    ArrayList<Post> getUsersPublicPosts(String username, Timestamp timestamp) throws SQLException;
    ArrayList<Post> getFeed(String username, Timestamp timestamp) throws SQLException;
    ArrayList<Post> getPostsByLocation(String location, String type, Timestamp timestamp) throws SQLException;
    ArrayList<Post> getPostsByCategory(String category, Timestamp timestamp);
    Image getPhoto(int id);
    Post getPost(int id);
    Post getPreviewPost(int id);
    //Retrieving data for edit post page
    void getDataForEdit(Post post);
    void setDescription(int id, String description);
    void setStatus(int id, String status);
    void deletePost(int id);
    Image getPreviewForLocation(String location, String type);
}
