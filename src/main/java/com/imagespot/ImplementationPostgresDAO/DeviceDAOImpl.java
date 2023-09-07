package com.imagespot.ImplementationPostgresDAO;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.DeviceDAO;
import com.imagespot.Model.Device;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeviceDAOImpl implements DeviceDAO {

    private final Connection con;


    public DeviceDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public Device addDevice(String brand, String model, String deviceType, String username) throws SQLException {
        int id = this.getDeviceID(brand, model);
        PreparedStatement st;
        ResultSet rs;
        String addNewDevice = "INSERT INTO device(brand, model, deviceType) VALUES(?, ?, ?) RETURNING iddevice";
        if(id == -1) {

            try {
                st = con.prepareStatement(addNewDevice);
                st.setString(1, brand);
                st.setString(2, model);
                st.setString(3, deviceType);
                rs = st.executeQuery();

                if(rs.next())
                    id = rs.getInt(1);

                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        addUserDevice(id, username);
        Device device = new Device(id, brand, model, deviceType);
        return device;
    }

    private void addUserDevice(int idDevice, String username) throws SQLException{
        String query = "INSERT INTO user_device(device, profile) VALUES(?, ?)";
        PreparedStatement st;

        st = con.prepareStatement(query);
        st.setInt(1, idDevice);
        st.setString(2, username);

        st.execute();
        st.close();
    }

    public void removeUserDevice(int idDevice, String username){
        String query = "DELETE FROM user_device WHERE device = ? AND profile = ?";
        PreparedStatement st;

        try {
            st = con.prepareStatement(query);
            st.setInt(1, idDevice);
            st.setString(2, username);
            st.execute();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
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

    public ArrayList<Device> getRecentUsedDevices(String username){
        ArrayList<Device> devices = new ArrayList<>();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT iddevice, brand, model, devicetype FROM device" +
                " JOIN user_device ON iddevice = device" +
                " WHERE profile = ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            rs = st.executeQuery();

            while(rs.next()){
                Device device = new Device();
                device.setIdDevice(rs.getInt(1));
                device.setBrand(rs.getString(2));
                device.setModel(rs.getString(3));
                device.setDeviceType(rs.getString(4));

                devices.add(device);
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return devices;
    }
}
