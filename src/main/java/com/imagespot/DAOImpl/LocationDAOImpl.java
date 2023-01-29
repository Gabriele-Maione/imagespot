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
    public int addLocation(Location location) {
        PreparedStatement st;
        String query = "INSERT INTO " +
                "location(country, state, city, postacode, latitude, longitude, formatted_address, road)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        ResultSet rs;
        int id = -1;
        try {
            st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, location.getCountry());
            st.setString(2, location.getState());
            st.setString(3, location.getCity());
            st.setString(4, location.getPostacode());
            st.setBigDecimal(5, location.getLatitude());
            st.setBigDecimal(6, location.getLongitude());
            st.setString(7, location.getFormatted_address());
            st.setString(8, location.getRoad());
            st.executeUpdate();
            rs = st.getGeneratedKeys();
            if(rs.next())
                id = rs.getInt(1);
            rs.close();
            st.close();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getTop(String location){
        ArrayList<String> top = new ArrayList<>();
        Statement st;
        ResultSet rs;
        String query = "SELECT " + location + ", count(*) as postN\n" +
                "FROM location join post p on location.idlocation = p.location\n" +
                "group by " + location +
                "\norder by postN DESC\n" +
                "LIMIT 10";
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()) {
                top.add(rs.getString(1));
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create retrieve top locations.", e);
        }
        return top;
    }
}
