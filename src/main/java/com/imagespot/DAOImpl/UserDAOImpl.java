package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.model.Device;
import com.imagespot.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.imagespot.Connection.ConnectionManager;

public class UserDAOImpl implements UserDAO {

    private Connection con;

    public UserDAOImpl() throws SQLException {
        con = ConnectionManager.getInstance().getConnection();
    }

    //returns 0 - user registered && 1 - email/username already exists
    @Override
    public boolean signup(String username, String name, String email, String password) {

        PreparedStatement st, st1;
        ResultSet rs, rs1;
        boolean flag = false;

        String mailCheck = "SELECT count(*) FROM account WHERE Username = ?";
        String usernameCheck = "SELECT count(*) FROM account WHERE Username = ?";
        String addNewUser = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";

        try {
            st = con.prepareStatement(usernameCheck);
            st1 = con.prepareStatement(mailCheck);

            st.setString(1, username);
            st1.setString(1, email);

            rs = st.executeQuery();
            rs1 = st1.executeQuery();

            if (rs.next() && rs1.next()) {
                if (rs.getInt(1) == 0 && rs1.getInt(1) == 0) {

                    //riutilizzo lo statement pke è importante riciclare
                    st = con.prepareStatement(addNewUser);
                    st.setString(1, username);
                    st.setString(2, name);
                    st.setString(3, email);
                    st.setString(4, password);
                    st.execute();
                    flag = true;
                }
            }
            st.close();
            st1.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    @Override
    public boolean login(String username, String password) throws SQLException {

        PreparedStatement st;
        ResultSet rs;
        boolean flag = false;

        String credentialsCheck = "SELECT * FROM account WHERE Username = ? AND password = ?";

        st = con.prepareStatement(credentialsCheck);
        st.setString(1, username);
        st.setString(2, password);
        rs = st.executeQuery();
        if(rs.next())
            flag = true;
        st.close();
        return flag;
    }

    @Override
    public void setBio(String username, String bio) {

        PreparedStatement st;
        String insertbio = "UPDATE account SET bio = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertbio);
            st.setString(1, bio);
            st.setString(2, username);
            st.executeQuery(insertbio);
            st.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setGender(String username, String gender) {

        PreparedStatement st;
        String insertbio = "UPDATE account SET gender = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertbio);
            st.setString(1, gender);
            st.setString(2, username);
            st.executeQuery(insertbio);
            st.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setAvatar(String username, File avatar) {

        PreparedStatement st;
        String insertbio = "UPDATE account SET gender = ? WHERE username = ?";

        try {
            st = con.prepareStatement(insertbio);
            st.setBinaryStream(1, new FileInputStream(avatar));
            st.setString(2, username);
            st.executeQuery(insertbio);
            st.close();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
