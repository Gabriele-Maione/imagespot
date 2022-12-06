package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.User;

import java.sql.SQLException;

public interface UserDAO {
    // true - user registered && false - username/email already exists
    public boolean signup(String username, String name, String email, String password) throws SQLException;
    public User login(String credentials, String password) throws SQLException;

}
