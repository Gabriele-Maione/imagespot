package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceDAOImpl {

    private final Connection con;


    public DeviceDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

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
}
