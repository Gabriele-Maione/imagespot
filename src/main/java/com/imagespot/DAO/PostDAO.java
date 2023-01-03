package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.Location;
import com.imagespot.model.Post;
import com.imagespot.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface PostDAO {

    public void addPost(File photo, String resolution, String description, int size, String extension, Timestamp posting_date,
                        String status, Device device, User profile) throws SQLException, IOException;
    public List<Post> getRecentPost() throws SQLException;

    public List<Post>getUsersPost(String username) throws SQLException;
    public List<Post> getUsersPublicPost(String username) throws SQLException;
    List<Post> getFeed(String username) throws SQLException;
    public InputStream getPhoto(int id);
    public Post getPost(int id);
}
