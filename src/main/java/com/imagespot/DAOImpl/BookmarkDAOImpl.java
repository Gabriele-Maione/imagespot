package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.BookmarkDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookmarkDAOImpl implements BookmarkDAO {

    private final Connection con;


    public BookmarkDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addBookmark(int idImage) {
        PreparedStatement st;
        String query = "INSERT INTO bookmark (username, idimage) VALUES (?, ?)";
        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setInt(2, idImage);

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeBookmark(int idImage) {
        PreparedStatement st;
        String query = "DELETE FROM bookmark WHERE username = ? AND idimage = ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setInt(2, idImage);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLiked(int idImage, User user) {
        boolean flag = false;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT * FROM bookmark WHERE username = ? AND idimage = ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, user.getUsername());
            st.setInt(2, idImage);
            rs = st.executeQuery();

            if (rs.next()) flag = true;
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    public ArrayList<Post> getUserBookmarks(int offset) {
        ArrayList<Post> bookmarks = new ArrayList<>();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT P.idimage FROM post P" +
                " JOIN bookmark B ON P.idimage = B.idimage WHERE username = ?" +
                " ORDER BY date DESC LIMIT 20 OFFSET ?";


        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setInt(2, offset);

            rs = st.executeQuery();
            while (rs.next()) {
                Post post;
                post = new PostDAOImpl().getPreviewPost(rs.getInt(1));

                bookmarks.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bookmarks;
    }

    @Override
    public ArrayList<User> getPostBookmarks(int id) {
        ArrayList<User> likes = new ArrayList<User>();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT username FROM bookmark WHERE idimage = ? ORDER BY date DESC";

        try {
            st = con.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            while (rs.next()) {
                User user;
                user = new UserDAOImpl().getUserInfoForPreview(rs.getString(1));
                likes.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return likes;

    }

    @Override
    public int getLikesCount(int idImage) {
        int count = 0;
        Statement st;
        ResultSet rs;
        String query = "SELECT count(*) FROM bookmark WHERE idimage = " + idImage;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to retrieve likes count.", e);
        }
        return count;
    }
}