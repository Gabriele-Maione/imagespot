package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.UserDAO;
import com.imagespot.model.Device;
import com.imagespot.model.User;

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
    public boolean signup(String username, String name, String email, String password) throws SQLException {

        PreparedStatement st, st1;
        ResultSet rs, rs1;
        boolean flag = false;

        String mailCheck = "SELECT count(*) FROM account WHERE Username = ?";
        String usernameCheck = "SELECT count(*) FROM account WHERE Username = ?";
        String addNewUser = "INSERT INTO account(Username, Name, Email, password) VALUES(?, ?, ?, ?)";

        st = con.prepareStatement(usernameCheck);
        st1 = con.prepareStatement(mailCheck);

        st.setString(1, username);
        st1.setString(1, email);

        rs = st.executeQuery();
        rs1 = st1.executeQuery();

        if(rs.next() && rs1.next()) {
            if(rs.getInt(1) == 0 && rs1.getInt(1) == 0) {

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

        return flag;
    }

    public User login(String username, String password) throws SQLException {

        PreparedStatement st;
        ResultSet rs;
        User user = null;

        String credentialsCheck = "SELECT * FROM account WHERE Username = ? AND password = ?";

        st = con.prepareStatement(credentialsCheck);
        st.setString(1, username);
        st.setString(2, password);
        rs = st.executeQuery();
        if(rs.next())
            user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
        st.close();
        return user;
    }

    public void addPhoto(String namePhoto, Device device) {
        //TODO
    }

}
