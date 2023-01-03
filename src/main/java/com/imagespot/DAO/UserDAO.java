package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface UserDAO {
    // true - user registered && false - username/email already exists
    boolean signup(String username, String name, String email, String password);

    boolean login(String credentials, String password) throws SQLException;

    void setBio(String username, String bio);

    void setGender(String username, String gender);

    void setAvatar(String username, File photo) throws IOException;

    void getUserInfo(String username) throws SQLException;

    User getUserInfoForPreview(String username) throws SQLException;

    List<User> findUsers(String username);

    boolean checkFollow(String username);
    void removeFollow(String username);
    void setFollow(String username);
    int userPostsCount(String username);
    int userFollowerCount(String username);
    int userFollowingCount(String username);

}
