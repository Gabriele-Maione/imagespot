package com.imagespot.DAO;

import com.imagespot.Model.Post;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaggedUserDAO {

    void addTag(String username, int idimage);

    ArrayList<Post> getTag(String username, int offset) throws SQLException;
}
