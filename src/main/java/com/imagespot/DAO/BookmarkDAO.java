package com.imagespot.DAO;

import com.imagespot.model.Post;
import com.imagespot.model.User;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public interface BookmarkDAO {
    void addBookmark(int idImage);
    void removeBookmark(int idImage);
    boolean isLiked(int idImage);
    ArrayList<Post> getUserBookmarks(Timestamp timestamp);
    ArrayList<User> getPostBookmarks(int id);
}
