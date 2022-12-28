package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface UserDAO {
    // true - user registered && false - username/email already exists
    public boolean signup(String username, String name, String email, String password);
    public boolean login(String credentials, String password) throws SQLException;
    public void setBio(String username, String bio);
    public void setGender(String username, String gender);
    public void setAvatar(String username, File photo) throws IOException;
    public void getUserInfo(String username) throws SQLException;
    public User getUserInfoForPreview(String username) throws SQLException;

}
