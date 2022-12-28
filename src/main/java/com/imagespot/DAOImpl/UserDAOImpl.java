package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Device;
import com.imagespot.model.User;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.imagespot.Connection.ConnectionManager;
import javafx.fxml.FXML;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;

public class UserDAOImpl implements UserDAO {

    private Connection con;

    public UserDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    //returns 0 - user registered && 1 - email/username already exists
    @Override
    public boolean signup(String username, String name, String email, String password) {

        PreparedStatement st, st1;
        ResultSet rs, rs1;
        boolean flag = false;

        String mailCheck = "SELECT count(*) FROM account WHERE Email = ?";
        String usernameCheck = "SELECT count(*) FROM account WHERE Username = ?";
        String addNewUser = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";

        try {
            st = con.prepareStatement(usernameCheck);
            st1 = con.prepareStatement(mailCheck);

            st.setString(1, username);
            st1.setString(1, email);

            rs = st.executeQuery();
            rs1 = st1.executeQuery();

            if (rs.next() && rs1.next()) {
                if (rs.getInt(1) == 0 && rs1.getInt(1) == 0) {

                    //riutilizzo lo statement pke è importante riciclare
                    st = con.prepareStatement(addNewUser);
                    st.setString(1, username);
                    st.setString(2, name);
                    st.setString(3, email);
                    st.setString(4, password);
                    st.execute();
                    flag = true;
                }
            }
            st.close();
            st1.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    @Override
    public boolean login(String username, String password) throws SQLException {

        PreparedStatement st;
        ResultSet rs;
        boolean flag = false;

        String credentialsCheck = "SELECT * FROM account WHERE Username = ? AND password = ?";

        st = con.prepareStatement(credentialsCheck);
        st.setString(1, username);
        st.setString(2, password);
        rs = st.executeQuery();
        if(rs.next())
            flag = true;
        st.close();
        return flag;
    }

    @Override
    public void setBio(String username, String bio) {

        PreparedStatement st;
        String insertbio = "UPDATE account SET bio = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertbio);
            st.setString(1, bio);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        ViewFactory.getInstance().getUser().setBio(bio);
    }

    @Override
    public void setGender(String username, String gender) {

        PreparedStatement st;
        String insertgender = "UPDATE account SET gender = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertgender);
            st.setString(1, gender);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        ViewFactory.getInstance().getUser().setGender(gender);
    }

    @Override
    public void setAvatar(String username, File avatar) throws IOException {

        PreparedStatement st;
        String insertavatar = "UPDATE account SET avatar = ? WHERE username = ?";

        BufferedImage bufferedImage = ImageIO.read(avatar.getAbsoluteFile());
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 500, 500, Scalr.OP_ANTIALIAS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", baos);
        InputStream preview = new ByteArrayInputStream(baos.toByteArray());

        try {
            st = con.prepareStatement(insertavatar);
            st.setBinaryStream(1, preview);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        ViewFactory.getInstance().getUser().setAvatar(preview);
    }

    @Override
    public void getUserInfo(String username) throws SQLException {

        PreparedStatement st;
        ResultSet rs;

        String query = "SELECT email, name, gender, bio, avatar FROM account WHERE Username = ?";

        st = con.prepareStatement(query);
        st.setString(1, username);
        rs = st.executeQuery();
        if (rs.next()) {
            ViewFactory.getInstance().getUser().setUsername(username);
            ViewFactory.getInstance().getUser().setEmail(rs.getString(1));
            ViewFactory.getInstance().getUser().setName(rs.getString(2));
            ViewFactory.getInstance().getUser().setGender(rs.getString(3));
            ViewFactory.getInstance().getUser().setBio(rs.getString(4));
            ViewFactory.getInstance().getUser().setAvatar(rs.getBinaryStream(5));
        }
        st.close();

    }

    public User getUserInfoForPreview(String username) throws SQLException {

        User user = new User();
        user.setUsername(username);
        PreparedStatement st;
        ResultSet rs;

        String query = "SELECT email, name, gender, bio, avatar FROM account WHERE Username = ?";

        st = con.prepareStatement(query);
        st.setString(1, username);
        rs = st.executeQuery();
        if (rs.next()) {
            user.setEmail(rs.getString(1));
            user.setName(rs.getString(2));
            user.setGender(rs.getString(3));
            user.setBio(rs.getString(4));
            user.setAvatar(rs.getBinaryStream(5));
        }
        st.close();

        return user;
    }

}
