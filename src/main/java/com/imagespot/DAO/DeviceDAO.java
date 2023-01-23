package com.imagespot.DAO;

import com.imagespot.model.Device;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface DeviceDAO {
    Device addDevice(String brand, String model, String deviceType, String username) throws SQLException;

    int getDeviceID(String brand, String model) throws SQLException;

    Device getDevice(int idDevice);

    ArrayList<Device> getRecentUsedDevices(String username);

    void removeUserDevice(int idDevice, String username);
}
