package com.imagespot.ImplementationPostgresDAO;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.View.ViewFactory;
import com.imagespot.Model.User;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.imagespot.Utils.Utils.photoScaler;

public class UserDAOImpl implements UserDAO {

    private final Connection con;

    public UserDAOImpl() {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public int signup(String username, String email, String name, String password) {
        PreparedStatement st;
        ResultSet rs;
        int flag = 0;
        String query = "SELECT insert_account(?, ?, ?, ?)";

        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setString(2, email);
            st.setString(3, name);
            st.setString(4, password);
            rs = st.executeQuery();
            if(rs.next()) flag = rs.getInt(1);
            st.close();
            rs.close();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to check credentials.", e);
        }
        return flag;
    }


    @Override
    public boolean login(String username, String password) {
        PreparedStatement st;
        ResultSet rs;
        boolean flag = false;

        String credentialsCheck = "SELECT username FROM account WHERE Username = ? AND password = crypt(?, password)";

        try {
            st = con.prepareStatement(credentialsCheck);

            st.setString(1, username);
            st.setString(2, password);
            rs = st.executeQuery();
            if (rs.next())
                flag = true;
            st.close();
            rs.close();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to check credentials.", e);
        }

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
    public User getUserInfo(String username) {
        PreparedStatement st;
        ResultSet rs;
        User user = new User();

        String query = "SELECT email, name, gender, bio, avatar FROM account WHERE Username = ?";

        try {
            st = con.prepareStatement(query);

        st.setString(1, username);
        rs = st.executeQuery();
        if (rs.next()) {
            user.setUsername(username);
            user.setEmail(rs.getString(1));
            user.setName(rs.getString(2));
            user.setGender(rs.getString(3));
            user.setBio(rs.getString(4));
            if (rs.getBinaryStream(5) != null)
                user.setAvatar(new Image(rs.getBinaryStream(5)));
        }
        st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
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

        String query = "SELECT avatar, username, name, bio FROM account " +
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
                user.setBio(rs.getString(4));
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
        String query = "SELECT name, username, avatar, bio FROM account AS A " +
                "JOIN following AS F ON A.username = F.idfollowing " +
                "WHERE F.nickname = '" + username + "' " +
                "ORDER BY followdate DESC";

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()){
                User user = new User();
                user.setName(rs.getString(1));
                user.setUsername(rs.getString(2));
                if(rs.getBinaryStream(3) != null)
                    user.setAvatar(new Image(rs.getBinaryStream(3)));
                user.setBio(rs.getString(4));
                followedUsers.add(user);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return followedUsers;
    }
}
