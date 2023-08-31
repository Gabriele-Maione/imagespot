package com.imagespot.ImplementationPostgresDAO;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.SubjectDAO;
import com.imagespot.Model.Subject;

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
        String query = "select insert_subject_post(?, ?, ?)";
        System.out.println(subject.getImageID());
        try {
            st = con.prepareStatement(query);
            st.setString(1, subject.getSubject());
            st.setString(2, subject.getCategory());
            st.setInt(3, subject.getImageID());
            st.execute();
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
        String query = "SELECT subject_id, category, name\n" +
                "FROM subject join subject_post sp on subject.subject_id = sp.subject\n" +
                "WHERE post = " + idimage;
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()) {
                subjects.add(new Subject(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjects;
    }
}
