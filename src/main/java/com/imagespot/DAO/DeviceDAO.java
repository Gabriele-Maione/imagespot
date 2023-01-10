package com.imagespot.DAO;

import com.imagespot.model.Device;

import java.sql.SQLException;

public interface DeviceDAO {
    int addDevice(String brand, String model, String deviceType) throws SQLException;

    int getDeviceID(String brand, String model) throws SQLException;

    Device getDevice(int idDevice);
}
