package com.imagespot.ImplementationPostgresDAO;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.TaggedUserDAO;
import com.imagespot.Model.Post;

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
        String query = "INSERT INTO tagged_user(nickname, idimage) VALUES (?, ?)";
        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setInt(2, idimage);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Post> getTag(String username, int offset) {
        ArrayList<Post> tag = new  ArrayList<>();
        PreparedStatement st;
        ResultSet rs;
        PostDAOImpl post = new PostDAOImpl();

        StringBuilder complexQuery = new StringBuilder("SELECT P.idimage FROM post P" +
                " JOIN tagged_user T ON P.idimage = T.idimage" +
                " WHERE nickname = ? AND status = 'Public'");

        complexQuery.append(" ORDER BY posting_date DESC LIMIT 20 OFFSET ?");

        try {
            st = con.prepareStatement(complexQuery.toString());
            st.setString(1, username);
            st.setInt(2, offset);
            rs = st.executeQuery();
            while (rs.next()) {
                tag.add(post.getPreviewPost(rs.getInt(1)));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
