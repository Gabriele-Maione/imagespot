package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.LocationDAO;
import com.imagespot.model.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocationDAOImpl implements LocationDAO {

    private final Connection con;


    public LocationDAOImpl() {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addLocation(Location location) {
        PreparedStatement st;
        String query = "INSERT INTO " +
                "location(country, state, city, postcode, latitude, longitude, formatted_address, road, post)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            st = con.prepareStatement(query);
            st.setString(1, location.getCountry());
            st.setString(2, location.getState());
            st.setString(3, location.getCity());
            st.setString(4, location.getPostcode());
            st.setBigDecimal(5, location.getLatitude());
            st.setBigDecimal(6, location.getLongitude());
            st.setString(7, location.getFormatted_address());
            st.setString(8, location.getRoad());
            st.setInt(9, location.getPost().getIdImage());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to add location.", e);
        }
    }

    public List<String> getTop(String location) {
        ArrayList<String> top = new ArrayList<>();
        Statement st;
        ResultSet rs;
        String query = "SELECT " + location + ", count(*) as postN\n" +
                "FROM location\n" +
                "WHERE " + location + " IS NOT NULL\n" +
                "group by " + location +
                "\norder by postN DESC\n" +
                "LIMIT 10";
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                top.add(rs.getString(1));
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create retrieve top locations.", e);
        }
        return top;
    }

    @Override
    public Location getLocation(int idPost) {

        Location location = null;
        Statement st;
        ResultSet rs;
        String query = "SELECT formatted_address FROM location WHERE post = " + idPost;

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            if (rs.next()) {
                location = new Location();
                location.setFormatted_address(rs.getString(1));
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to retrieve the location.", e);
        }
        return location;
    }
}
