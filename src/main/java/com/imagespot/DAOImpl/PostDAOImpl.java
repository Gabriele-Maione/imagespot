package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.PostDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Device;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.scene.image.Image;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import static com.imagespot.Utils.Utils.photoScaler;

public class PostDAOImpl implements PostDAO {

    private final Connection con;

    public PostDAOImpl() {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addPost(File photo, Post post, Device device, User profile) throws SQLException, IOException {
        int id = -1;
        PreparedStatement st;
        ResultSet rs;
        String insert = ("INSERT INTO Post (photo, resolution, description, size, extension, posting_date," +
                " status, device, profile, preview, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING idimage");

        //scaling image and deserialize from bufferedImage to InputStream


        InputStream preview = photoScaler(photo);
        st = con.prepareStatement(insert);

        byte[] previewBytes = IOUtils.toByteArray(preview);

        st.setBinaryStream(1, new FileInputStream(photo));
        st.setString(2, post.getResolution());
        st.setString(3, post.getDescription());
        st.setInt(4, post.getSize());
        st.setString(5, post.getExtension());
        st.setTimestamp(6, post.getDate());
        st.setString(7, post.getStatus());
        st.setInt(8, device.getIdDevice());
        st.setString(9, profile.getUsername());
        st.setBinaryStream(10, new ByteArrayInputStream(previewBytes));
        System.out.println(post.getLocation().getIdLocation());
        st.setInt(11, post.getLocation().getIdLocation());
        rs = st.executeQuery();

        if (rs.next()) id = rs.getInt(1);
        st.close();

        post.setIdImage(id);
        post.setPreview(new Image(new ByteArrayInputStream(previewBytes)));
        post.setPhoto(new Image(photo.getAbsolutePath()));
    }

    public ArrayList<Post> getRecentPosts(Timestamp timestamp) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post " +
                " WHERE status = 'Public' AND profile NOT IN('" +
                ViewFactory.getInstance().getUser().getUsername() + "')";

        return getPreviews(query, timestamp);
    }

    //Retrieve user's personal posts
    @Override
    public ArrayList<Post> getUserPosts(String username, Timestamp timestamp) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE profile = '" + username + "'";

        return getPreviews(query, timestamp);
    }

    @Override
    public ArrayList<Post> getUsersPublicPosts(String username, Timestamp timestamp) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE " +
                "status = 'Public' AND profile = '" + username + "'";

        return getPreviews(query, timestamp);
    }

    @Override
    public ArrayList<Post> getFeed(String username, Timestamp timestamp) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage" +
                " FROM post" +
                " WHERE status = 'Public'" +
                " AND profile IN (SELECT idfollowing" +
                " FROM following" +
                " WHERE nickname = '" + username + "')";

        return getPreviews(query, timestamp);
    }

    @Override
    public ArrayList<Post> getPostsByLocation(String location, String type, Timestamp timestamp) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage" +
                " FROM post JOIN location ON idlocation = location" +
                " WHERE status = 'Public'" +
                " AND " + type + " = '" + location + "'";
        return getPreviews(query, timestamp);
    }

    @Override
    public ArrayList<Post> getPostsByCategory(String category, Timestamp timestamp) {
        String query = "SELECT preview, profile, posting_date, idimage" +
                " FROM post JOIN subject ON idimage = image" +
                " WHERE status = 'Public'" +
                " AND category = '" + category + "'";
        return getPreviews(query, timestamp);
    }


    public ArrayList<Post> getPreviews(String query, Timestamp timestamp) {
        ArrayList<Post> ls = new ArrayList<>();
        Post post;
        Statement st;
        ResultSet rs;
        try {
            st = con.createStatement();

            StringBuilder complexQuery = new StringBuilder(query);

            if (timestamp != null)
                complexQuery.append(" AND posting_date < '").append(timestamp).append("'");
            complexQuery.append(" ORDER BY posting_date DESC LIMIT 20");

            rs = st.executeQuery(complexQuery.toString());

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
            rs.close();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to load posts.", e);
        }
        return ls;
    }

    //split query in 2 parts so u can first load user info and only after the full size photo
    @Override
    public Post getPost(int id) {
        Post post = new Post();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT profile, description, extension, idimage, posting_date, device FROM post WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(1)));
                post.setDescription(rs.getString(2));
                post.setExtension(rs.getString(3));
                post.setIdImage(rs.getInt(4));
                post.setDate(rs.getTimestamp(5));
                post.setLikes(new BookmarkDAOImpl().getPostBookmarks(post.getIdImage()));
                post.setDevice(new DeviceDAOImpl().getDevice(rs.getInt(6)));
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

    @Override
    public Post getPreviewPost(int id) {
        Post post = null;
        Statement st;
        ResultSet rs;
        String query = "SELECT preview, profile, posting_date, idimage FROM post " +
                "WHERE idimage = '" + id + "'";

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                post = new Post();
                post.setIdImage(rs.getInt(4));
                post.setPreview(new Image(rs.getBinaryStream(1)));
                post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(2)));
                post.setDate(rs.getTimestamp(3));
                post.setIdImage(rs.getInt(4));
            }
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    @Override
    public void getDataForEdit(Post post) {
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT status, description FROM post WHERE idimage = ?";

        try {
            st = con.prepareStatement(query);
            st.setInt(1, post.getIdImage());
            rs = st.executeQuery();
            if (rs.next()) {
                post.setStatus(rs.getString(1));
                post.setDescription(rs.getString(2));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDescription(int id, String description) {
        PreparedStatement st;
        String query = "UPDATE post SET description = ? WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setString(1, description);
            st.setInt(2, id);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setStatus(int id, String status) {
        PreparedStatement st;
        String query = "UPDATE post SET status = ? WHERE idimage = ?";
        try {
            st = con.prepareStatement(query);
            st.setString(1, status);
            st.setInt(2, id);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePost(int id) {
        Statement st;
        String query = "DELETE FROM post WHERE idimage = '" + id + "'";
        System.out.println(query);
        try {
            st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Image getPreviewForLocation(String location, String type) {
        PreparedStatement st;
        ResultSet rs;
        Image img = null;
        String query = "SELECT preview FROM post JOIN location ON post.location = location.idlocation\n" +
                "WHERE " + type + " = ? AND status = 'Public'\n" +
                "ORDER BY posting_date DESC\n" +
                "LIMIT 1";
        try {
            st = con.prepareStatement(query);
            st.setString(1, location);
            rs = st.executeQuery();
            if (rs.next())
                img = new Image(rs.getBinaryStream(1));
            st.close();
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return img;
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
