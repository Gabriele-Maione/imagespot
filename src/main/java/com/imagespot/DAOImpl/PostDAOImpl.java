package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.PostDAO;
import com.imagespot.model.Device;
import com.imagespot.model.Location;
import com.imagespot.model.Post;
import com.imagespot.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAOImpl implements PostDAO {

    private Connection con;

    public PostDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addPost(File photo, String resolution, String description, int size, String extension,
                        Timestamp posting_date, String status, Device device, User profile) throws SQLException, FileNotFoundException {

        PreparedStatement st;
        String insert = ("INSERT INTO Post (photo, resolution, description, size, extension, posting_date," +
                " status, device, profile) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

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
        st.execute();
        st.close();
    }

    public List<Post> getRecentPost() throws SQLException {

        List<Post> ls = new ArrayList<>();
        Post post;
        Statement st;
        ResultSet rs;
        String query = "SELECT photo, profile FROM post WHERE status = 'Public' ORDER BY posting_date DESC LIMIT 20";
        st = con.createStatement();
        rs = st.executeQuery(query);

        while(rs.next()) {
            post = new Post();
            post.setProfile(new UserDAOImpl().getUserInfo(rs.getString(2)));
            post.setPhoto(rs.getBinaryStream(1));
            ls.add(post);
        }

        st.close();
        return ls;
    }

}
