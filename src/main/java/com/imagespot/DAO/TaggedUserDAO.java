package com.imagespot.DAO;

import com.imagespot.model.Post;
import com.imagespot.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public interface TaggedUserDAO {

    void addTag(String username, int idimage);

    ArrayList<Post> getTag(String username, Timestamp timestamp) throws SQLException;
}
