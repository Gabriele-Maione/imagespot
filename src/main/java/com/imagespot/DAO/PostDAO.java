package com.imagespot.DAO;

import com.imagespot.model.Device;
import com.imagespot.model.Location;
import com.imagespot.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface PostDAO {

    public void addPost(File photo, String resolution, String description, int size,
                        String extension, Timestamp posting_date, String status,
                        Location location, Device device, User profile) throws SQLException, FileNotFoundException;
}
