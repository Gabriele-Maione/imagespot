package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.PostDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Device;
import com.imagespot.model.Location;
import com.imagespot.model.Post;
import com.imagespot.model.User;
import javafx.scene.image.Image;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.imagespot.Utils.Utils.photoScaler;

public class PostDAOImpl implements PostDAO {

    private Connection con;

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

        List<Post> ls = new ArrayList<>();
        Post post;
        Statement st;
        ResultSet rs;
        String query = "SELECT preview, profile, posting_date, description FROM post WHERE status = 'Public' AND idimage>18 ORDER BY posting_date DESC LIMIT 20";
        st = con.createStatement();
        rs = st.executeQuery(query);

        while(rs.next()) {
            post = new Post();
            post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(2)));
            post.setPreview(rs.getBinaryStream(1));
            post.setDate(rs.getTimestamp(3));
            post.setDescription(rs.getString(4));
            ls.add(post);
        }

        st.close();
        return ls;
    }

    public List<Post>getUsersPost(String username) throws SQLException {

        List<Post> ls = new ArrayList<>();
        Post post;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT preview, profile, posting_date, description FROM post WHERE profile = ? ORDER BY posting_date DESC LIMIT 20";
        st = con.prepareStatement(query);
        st.setString(1, username);
        rs = st.executeQuery();
        while(rs.next()) {
            post = new Post();
            post.setProfile(new UserDAOImpl().getUserInfoForPreview(rs.getString(2)));
            post.setPreview(rs.getBinaryStream(1));
            post.setDate(rs.getTimestamp(3));
            post.setDescription(rs.getString(4));
            ls.add(post);
        }

        st.close();
        return ls;
    }

}
