package com.imagespot.DAO;

import com.imagespot.model.Device;

public interface DeviceDAO {
    public int addDevice(String brand, String model, String deviceType);
    public int getDevice(String brand, String model);
}
