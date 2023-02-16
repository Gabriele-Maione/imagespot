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
    public void addPost(File photo, Post post, Device device, User profile) {
        PreparedStatement st;
        ResultSet rs;
        String insert = ("INSERT INTO Post (photo, resolution, description, size, extension, status, device, profile, preview)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING idimage, posting_date");

        //scaling image and deserialize from bufferedImage to InputStream
        InputStream preview = null;
        try {
            preview = photoScaler(photo);

            st = con.prepareStatement(insert);

            byte[] previewBytes = IOUtils.toByteArray(preview);

            st.setBinaryStream(1, new FileInputStream(photo));
            st.setString(2, post.getResolution());
            st.setString(3, post.getDescription());
            st.setInt(4, post.getSize());
            st.setString(5, post.getExtension());
            st.setString(6, post.getStatus());
            st.setInt(7, device.getIdDevice());
            st.setString(8, profile.getUsername());
            st.setBinaryStream(9, new ByteArrayInputStream(previewBytes));

            rs = st.executeQuery();

            if (rs.next()){
                post.setIdImage(rs.getInt(1));
                post.setDate(rs.getTimestamp(2));
            }

            post.setPreview(new Image(new ByteArrayInputStream(previewBytes)));
            post.setPhoto(new Image(photo.getAbsolutePath()));
            st.close();
            rs.close();
        } catch (IOException | SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to add new post", e);
        }
    }

    public ArrayList<Post> getRecentPosts(int offset) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post " +
                " WHERE status = 'Public' AND profile NOT IN('" +
                ViewFactory.getInstance().getUser().getUsername() + "')";

        return getPreviews(query, offset);
    }

    //Retrieve user's personal posts
    @Override
    public ArrayList<Post> getUserPosts(String username, int offset) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE profile = '" + username + "'";

        return getPreviews(query, offset);
    }

    @Override
    public ArrayList<Post> getUsersPublicPosts(String username, int offset) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage FROM post WHERE " +
                "status = 'Public' AND profile = '" + username + "'";

        return getPreviews(query, offset);
    }

    @Override
    public ArrayList<Post> getFeed(String username, int offset) throws SQLException {
        String query = "SELECT preview, profile, posting_date, idimage" +
                " FROM post" +
                " WHERE status = 'Public'" +
                " AND profile IN (SELECT idfollowing" +
                " FROM following" +
                " WHERE nickname = '" + username + "')";

        return getPreviews(query, offset);
    }

    @Override
    public ArrayList<Post> getPostsByLocation(String location, String type, int offset) {
        String query = "SELECT preview, profile, posting_date, idimage" +
                " FROM post p JOIN location l ON p.idimage = l.post" +
                " WHERE status = 'Public'" +
                " AND " + type + " = '" + location + "'";
        return getPreviews(query, offset);
    }

    @Override
    public ArrayList<Post> getPostsByCategory(String category, int offset) {
        String query = "SELECT DISTINCT preview, profile, posting_date, idimage" +
                " from post join subject_post sp on post.idimage = sp.post join subject s on sp.subject = s.subject_id" +
                " WHERE status = 'Public'" +
                " AND category = '" + category + "'";
        return getPreviews(query, offset);
    }

    @Override
    public ArrayList<Post> getPostsBySubject(int subject_id, int offset) {
        String query = "SELECT DISTINCT preview, profile, posting_date, idimage" +
                " from post join subject_post sp on post.idimage = sp.post" +
                " WHERE status = 'Public'" +
                " AND subject = '" + subject_id + "'";
        System.out.println(query);
        return getPreviews(query, offset);
    }

    public ArrayList<Post> getPreviews(String query, int offset) {
        ArrayList<Post> ls = new ArrayList<>();
        Post post;
        Statement st;
        ResultSet rs;
        try {
            st = con.createStatement();

            StringBuilder complexQuery = new StringBuilder(query);

            complexQuery.append(" ORDER BY posting_date DESC LIMIT 20 OFFSET ").append(offset);

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
                post.setLikesNumber(new BookmarkDAOImpl().getLikesCount(post.getIdImage()));
                post.setDevice(new DeviceDAOImpl().getDevice(rs.getInt(6)));
                post.setTaggedUsers(getTaggedUsers(post.getIdImage()));
                post.setSubjects(new SubjectDAOImpl().getSubjects(post.getIdImage()));
                post.setLocation(new LocationDAOImpl().getLocation(post.getIdImage()));
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
        try {
            st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Image getPreviewForLocation(String location, String type) {
        PreparedStatement st;
        ResultSet rs;
        Image img = null;
        String query = "SELECT preview FROM post JOIN location ON post.idimage = location.post\n" +
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
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to get previews.", e);

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

    public ArrayList<User> getTaggedUsers(int idimage) {
        ArrayList<User> users = new ArrayList<>();
        UserDAOImpl userDAO = new UserDAOImpl();
        Statement st;
        ResultSet rs;
        String query = "SELECT nickname FROM tagged_user WHERE idimage = " + idimage;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                users.add(userDAO.getUserInfoForPreview(rs.getString(1)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
