package com.imagespot.DAO;

import com.imagespot.Model.Post;
import com.imagespot.Model.User;

import java.util.ArrayList;

public interface BookmarkDAO {
    void addBookmark(int idImage);
    void removeBookmark(int idImage);
    boolean isLiked(int idImage, User user);
    int getLikesCount(int idImage);
    ArrayList<Post> getUserBookmarks(int offset);
    ArrayList<User> getPostBookmarks(int id);
}
