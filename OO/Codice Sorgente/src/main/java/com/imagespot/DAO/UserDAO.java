package com.imagespot.DAO;

import com.imagespot.Model.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    // true - user registered && false - username/email already exists
    int signup(String username, String email, String name, String password);

    boolean login(String credentials, String password) throws SQLException;

    void setBio(String username, String bio);

    void setGender(String username, String gender);

    void setAvatar(String username, File photo) throws IOException;
    void deleteAvatar(String username);
    void setName(String username, String name);

    User getUserInfo(String username) throws SQLException;

    User getUserInfoForPreview(String username) throws SQLException;

    List<User> findUsers(String username);

    boolean checkFollow(String username);
    void removeFollow(String username);
    void setFollow(String username);
    int[] retrieveUserStats(String username);
    List<User> getFollowedUsers(String username);

}
