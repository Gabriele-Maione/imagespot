package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.SubjectDAO;
import com.imagespot.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectDAOImpl implements SubjectDAO {

    private final Connection con;

    public SubjectDAOImpl() {
        con = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public void addSubject(Subject subject) {
        PreparedStatement st;
        String query = "INSERT INTO subject (category, subject, image) VALUES (?, ?, ?)";

        try {
            st = con.prepareStatement(query);
            st.setString(1, subject.getCategory());
            st.setString(2, subject.getSubject());
            st.setInt(3, subject.getImageID());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(ConnectionManager.class.getName());
            logger.log(Level.SEVERE, "Failed to insert subject into a database.", e);
        }
    }
}
