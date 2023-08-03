package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface PostDAO {
    void addPost(String photo, Post post, Device device, User profile) throws SQLException, IOException;
    ArrayList<Post> getRecentPosts(int offset) throws SQLException;
    ArrayList<Post>getUserPosts(String username, int offset) throws SQLException;
    ArrayList<Post> getUsersPublicPosts(String username, int offset) throws SQLException;
    ArrayList<Post> getFeed(String username, int offset) throws SQLException;
    ArrayList<Post> getPostsByLocation(String location, String type, int offset) throws SQLException;
    ArrayList<Post> getPostsByCategory(String category, int offset);
    Image getPhoto(int id);
    ArrayList<Post> getPostsBySubject(int subject_id, int offset);
    Post getPost(int id);
    Post getPreviewPost(int id);
    //Retrieving data for edit post page
    void getDataForEdit(Post post);
    void setDescription(int id, String description);
    void setStatus(int id, String status);
    void deletePost(int id);
    Image getPreviewForLocation(String location, String type);
    ArrayList<User> getTaggedUsers(int id);
}
