package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.TaggedUserDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;

import java.sql.*;
import java.util.ArrayList;

public class TaggedUserDAOImpl implements TaggedUserDAO {

    private final Connection con;

    public TaggedUserDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addTag(String username, int idimage) {
        PreparedStatement st;
        String query = "INSERT INTO taggeduser(nickname, idimage) VALUES (?, ?)";
        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setInt(2, idimage);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to insert tagged user to database");
        }
    }

    public ArrayList<Post> getTag() throws SQLException {
        ArrayList<Post> tag = new  ArrayList<Post>();
        PreparedStatement st;
        ResultSet rs;
        PostDAOImpl post = new PostDAOImpl();
        String query = "SELECT idimage FROM taggeduser WHERE nickname = ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            rs = st.executeQuery();
            while (rs.next()) {

                tag.add(post.getPreviewPost(rs.getInt(1)));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to retrieve data from TaggedUser table");
        }

        return tag;
    }
}
