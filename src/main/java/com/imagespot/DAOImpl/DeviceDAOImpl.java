package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.DeviceDAO;
import com.imagespot.model.Device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceDAOImpl implements DeviceDAO {

    private final Connection con;


    public DeviceDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public int addDevice(String brand, String model, String deviceType) throws SQLException {

        int i = this.getDeviceID(brand, model);
        PreparedStatement st;
        String addNewDevice = "INSERT INTO device(Brand, Model, deviceType) VALUES(?, ?, ?)";
        if(i == -1) {

            try {
                st = con.prepareStatement(addNewDevice);
                st.setString(1, brand);
                st.setString(2, model);
                st.setString(3, deviceType);
                st.execute();
                st.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            i = this.getDeviceID(brand, model);
        }
        return i;
    }

    @Override
    public int getDeviceID(String brand, String model) throws SQLException {

        int id = -1;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT iddevice FROM device WHERE Brand = ? AND Model = ?";
        st = con.prepareStatement(query);
        st.setString(1, brand);
        st.setString(2, model);
        rs = st.executeQuery();
        if(rs.next()) {
            id = rs.getInt(1);
        }
        st.close();
        return id;
    }

    @Override
    public Device getDevice(int idDevice) {
        Device device = null;
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT brand, model, devicetype FROM device WHERE iddevice = ?";

        try {
            st = con.prepareStatement(query);
            st.setInt(1, idDevice);
            rs = st.executeQuery();
            if(rs.next()) {
                device = new Device();
                device.setBrand(rs.getString(1));
                device.setModel(rs.getString(2));
                device.setDeviceType(rs.getString(3));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return device;
    }
}
