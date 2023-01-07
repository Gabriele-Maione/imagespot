package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.PostDAO;
import com.imagespot.model.Device;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.imagespot.Utils.Utils.photoScaler;

public class PostDAOImpl implements PostDAO {

    private final Connection con;

    public PostDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addPost(File photo, String resolution, String description, int size, String extension,
                        Timestamp posting_date, String status, Device device, User profile) throws SQLException, IOException {

        PreparedStatement st;
        String insert = ("INSERT INTO Post (photo, resolution, description, size, extension, posting_date," +
                " status, device, profile, preview) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        //scaling image and deserialize from bufferedImage to InputStream
        InputStream preview = photoScaler(photo);

        st = con.prepareStatement(insert);

        st.setBinaryStream(1, new FileInputStream(photo));
        st.setString(2, resolution);
        st.setString(3, description);
        st.setInt(4, size);
        st.setString(5, extension);
        st.setTimestamp(6, posting_date);
        st.setString(7, status);
        st.setInt(8, device.getIdDevice());
        st.setString(9, profile.getUsername());
        st.setBinaryStream(10, preview);
        st.execute();
        st.close();
    }

    public List<Post> getRecentPost() throws SQLException {

        String query = "SELECT preview, profile, posting_date, idimage FROM post " +
                "WHERE status = 'Public' ORDER BY posting_date DESC LIMIT 20";
        return getPreviews(query);
    }
    @Override
    public List<Post> getUsersPost(String username) throws SQLException {

        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE profile = '"
                + username + "' ORDER BY posting_date DESC LIMIT 20";
        return getPreviews(query);
    }

    @Override
    public List<Post> getUsersPublicPost(String username) throws SQLException {

        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE " +
                "status = 'Public' AND profile = '" + username + "' ORDER BY posting_date DESC LIMIT 20";
        return getPreviews(query);
    }

    @Override
    public List<Post> getFeed(String username) throws SQLException {

        String query = "SELECT preview, profile, posting_date, idimage\n" +
                "FROM post\n" +
                "WHERE status = 'Public'\n" +
                "  AND profile IN (SELECT idfollowing\n" +
                "                  FROM following\n" +
                "                  WHERE nickname = '" + username + "')\n" +
                "ORDER BY posting_date DESC\n" +
                "LIMIT 20";
        return getPreviews(query);
    }

    public List<Post> getPreviews(String query) throws SQLException {

        List<Post> ls = new ArrayList<>();
        Post post;
        Statement st;
        ResultSet rs;
        st = con.createStatement();
        rs = st.executeQuery(query);

        while (rs.next()) {
            post = new Post();
            post.setIdImage(rs.getInt(4));
            if (rs.getBinaryStream(1) != null)
                post.setPreview(new Image(rs.getBinaryStream(1)));
            post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(2)));
            post.setDate(rs.getTimestamp(3));
            post.setIdImage(rs.getInt(4));
            ls.add(post);
        }

        st.close();
        return ls;
    }

    //split query in 2 parts so u can first load user info and only after the full size photo
    @Override
    public Post getPost(int id) {
        Post post = new Post();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT profile, description, extension, idimage, device FROM post WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(1)));
                post.setDescription(rs.getString(2));
                post.setExtension(rs.getString(3));
                post.setIdImage(rs.getInt(4));
                //TODO: add device dao and things
                //post.setDevice();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }
    @Override
    public Image getPhoto(int id) {

        Image output = null;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT photo FROM post WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                output = (new Image(rs.getBinaryStream(1)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public InputStream getPhotoFile(int id) {

        InputStream output = null;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT photo FROM post WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                output = rs.getBinaryStream(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
}
