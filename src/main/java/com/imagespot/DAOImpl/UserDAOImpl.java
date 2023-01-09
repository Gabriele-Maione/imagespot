package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.User;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.imagespot.Utils.Utils.photoScaler;

public class UserDAOImpl implements UserDAO {

    private final Connection con;

    public UserDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    // false -> user registered
    // true -> email/username already exists
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
        String insertGender = "UPDATE account SET gender = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertGender);
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
        String insertAvatar = "UPDATE account SET avatar = ? WHERE username = ?";

        InputStream preview = photoScaler(avatar);

        try {
            st = con.prepareStatement(insertAvatar);
            st.setBinaryStream(1, preview);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteAvatar(String username) {
        Statement st;
        String deleteAvatar = "UPDATE account SET avatar = NULL WHERE username = '" + username + "'";


        try {
            st = con.createStatement();
            st.executeUpdate(deleteAvatar);
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setName(String username, String name) {
        PreparedStatement st;
        String query = "UPDATE account SET name = ? WHERE username = ?";
        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, username);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            if (rs.getBinaryStream(5) != null)
                ViewFactory.getInstance().getUser().setAvatar(new Image(rs.getBinaryStream(5)));
        }
        st.close();

    }

    public User getUserInfoForPreview(String username) {
        User user = new User();
        user.setUsername(username);
        PreparedStatement st;
        ResultSet rs;

        String query = "SELECT email, name, gender, bio, avatar FROM account WHERE Username = ?";

        try {
            st = con.prepareStatement(query);

            st.setString(1, username);
            rs = st.executeQuery();
            if (rs.next()) {
                user.setEmail(rs.getString(1));
                user.setName(rs.getString(2));
                user.setGender(rs.getString(3));
                user.setBio(rs.getString(4));
            if(rs.getBinaryStream(5) != null)
                user.setAvatar(new Image(rs.getBinaryStream(5)));
            }
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public List<User> findUsers(String searchedUser) {
        List<User> ls = new ArrayList<>();
        PreparedStatement st;
        ResultSet rs;

        String query = "SELECT avatar, username, name FROM account " +
                "WHERE (username iLIKE '%'||?||'%' OR name iLIKE '%'||?||'%' )" +
                "AND username NOT IN(?) ORDER BY username LIMIT 10";
        try {
            st = con.prepareStatement(query);
            st.setString(1, searchedUser);
            st.setString(2, searchedUser);
            st.setString(3, ViewFactory.getInstance().getUser().getUsername());
            rs = st.executeQuery();
            while (rs.next()) {
                User user = new User();
                if (rs.getBinaryStream(1) != null)
                    user.setAvatar(new Image(rs.getBinaryStream(1)));
                user.setUsername(rs.getString(2));
                user.setName(rs.getString(3));
                ls.add(user);
            }
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ls;
    }

    public boolean checkFollow(String username) {
        String query = "SELECT * FROM FOLLOWING WHERE nickname = ? AND idfollowing = ?";
        PreparedStatement st;
        ResultSet rs;
        boolean flag = false;
        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setString(2, username);
            rs = st.executeQuery();

            if(rs.next())  flag = true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  flag;
    }

    public void removeFollow(String username) {
        String query = "DELETE FROM following WHERE nickname = ? AND idfollowing = ? ";
        PreparedStatement st;

        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setString(2, username);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFollow(String username) {
        String query = "INSERT INTO following(nickname, idfollowing, followdate) VALUES (?, ?, ?)";
        PreparedStatement st;

        try {
            st = con.prepareStatement(query);
            st.setString(1, ViewFactory.getInstance().getUser().getUsername());
            st.setString(2, username);
            st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int[] retrieveUserStats(String username) {
        Statement st;
        ResultSet rs;
        String query = "SELECT * FROM user_stats WHERE username = '" + username + "'";

        int[] stats = new int[3];

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            if (rs.next()) {
                stats[0] = rs.getInt(2); //number of posts
                stats[1] = rs.getInt(3); //number of followers
                stats[2] = rs.getInt(4); //number of followed users
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stats;
    }
    @Override
    public List<User> getFollowedUsers(String username){
        List<User> followedUsers = new ArrayList<>();
        Statement st;
        ResultSet rs;
        String query = "SELECT name, username, avatar FROM account AS A " +
                "JOIN following AS F ON A.username = F.idfollowing " +
                "WHERE F.nickname = '" + username + "'";

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            
            while (rs.next()){
                User user = new User();
                user.setName(rs.getString(1));
                user.setUsername(rs.getString(2));
                if(rs.getBinaryStream(3) != null)
                    user.setAvatar(new Image(rs.getBinaryStream(3)));
                followedUsers.add(user);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return followedUsers;
    }
}
