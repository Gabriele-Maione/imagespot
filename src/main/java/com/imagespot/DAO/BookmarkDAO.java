package com.imagespot.DAO;

public interface BookmarkDAO {
    void addBookmark(int idImage);
    void removeBookmark(int idImage);
    boolean isLiked(int idImage);
    void getUserBookmarks();
}
