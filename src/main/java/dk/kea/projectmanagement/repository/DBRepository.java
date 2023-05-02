package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.DBManager;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Repository("database")
public class DBRepository {

    public List<User> getAllUsers() {
        try {
            Connection con = DBManager.getConnection();
            String SQL = "SELECT * FROM user;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int ID = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                users.add(new User(ID, username, password, firstName, lastName, birthday, role));
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
