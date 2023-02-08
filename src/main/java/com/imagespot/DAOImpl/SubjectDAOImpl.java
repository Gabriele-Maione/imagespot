package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.SubjectDAO;
import com.imagespot.model.Subject;
import com.imagespot.model.User;

import java.sql.*;
import java.util.ArrayList;
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
        System.out.println(subject.getImageID());
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

    public ArrayList<Subject> getSubjects(int idimage) {
        ArrayList<Subject> subjects = new ArrayList<>();
        Statement st;
        ResultSet rs;
        String query = "SELECT category, subject FROM subject WHERE image = " + idimage;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()) {
                subjects.add(new Subject(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjects;
    }
}
