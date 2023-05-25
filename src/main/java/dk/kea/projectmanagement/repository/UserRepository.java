package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository("user_repo")
public class UserRepository implements IUserRepository {

    private final DBManager dbManager;

    public UserRepository(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<User> getAllUsers() {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT * FROM user;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday") == null ? null : rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                User user = new User(username, password, firstName, lastName, birthday, role);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User getUserByID(int id) {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT * FROM user WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday") == null ? null : rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                return new User(username, password, firstName, lastName, birthday, role);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User login(String username, String password) throws LoginSampleException {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT * FROM user WHERE username = ? AND password = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday") == null ? null : rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                User user = new User(username, password, firstName, lastName, birthday, role);
                user.setId(id);
                return user;
            } else {
                throw new LoginSampleException("Incorrect username or password");
            }
        } catch (SQLException ex) {
            throw new LoginSampleException("Incorrect username or password");
        }
    }

    @Override
    public void createUser(User form) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            if (con == null) {
                throw new RuntimeException("Could not create a database connection");
            }
            con.setAutoCommit(false);
            String SQL = "INSERT INTO user (username, password, first_name, last_name, birthday, role) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getUsername());
            ps.setString(2, form.getPassword());
            ps.setString(3, form.getFirstName());
            ps.setString(4, form.getLastName());
            ps.setDate(5, form.getBirthday() != null ? Date.valueOf(form.getBirthday()) : null);
            ps.setString(6, form.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Could not create user");
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                form.setId(generatedKeys.getInt(1));
            } else {
                throw new RuntimeException("Could not create user");
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not create user", e);
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public User editUser(User form, int userId) {
        Connection con = null;
        System.out.println(form.getFirstName() + ' ' +  form.getLastName() + ' ' +  form.getBirthday() + ' ' + userId);
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE user SET username = ?, password = ?, first_name = ?, last_name = ?, birthday = ?, role = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, form.getUsername() != null ? form.getUsername() : getUserByID(userId).getUsername());
            ps.setString(2, form.getPassword() != null ? form.getPassword() : getUserByID(userId).getPassword());
            ps.setString(3, form.getFirstName() != null ? form.getFirstName() : getUserByID(userId).getFirstName());
            ps.setString(4, form.getLastName() != null ? form.getLastName() : getUserByID(userId).getLastName());
            ps.setDate(5, form.getBirthday() != null ? Date.valueOf(form.getBirthday()) : Date.valueOf(getUserByID(userId).getBirthday()));
            ps.setString(6, form.getRole() != null ? form.getRole() : getUserByID(userId).getRole());
            ps.setInt(7, userId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                con.commit();
                return getUserByID(userId);
            } else {
                throw new RuntimeException("Could not edit user");
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not edit user", e);
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void deleteUser(int id) {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "DELETE FROM user WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Could not delete user");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete user", e);
        }
    }

    @Override
    public List<User> getAllMembers() {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT * FROM user WHERE role = 'project_member';";
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday") == null ? null : rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                User user = new User(username, password, firstName, lastName, birthday, role);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<User> getMembersOfProject(int projectId) {
        try{
            Connection con = dbManager.getConnection();
            String SQL = "SELECT u.* FROM user u JOIN project_user pu ON u.id = pu.user_id WHERE pu.project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday") == null ? null : rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                User user = new User(username, password, firstName, lastName, birthday, role);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isUserMemberOfProject(int userId, int projectId) {
        try{
            Connection con = dbManager.getConnection();
            String SQL = "SELECT * FROM project_user WHERE user_id = ? AND project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, userId);
            ps.setInt(2, projectId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User getAssignedUserBySubtaskId(int subtaskId) {
        Connection con = null;
        User user = null;
        try {
            con = dbManager.getConnection();
            String SQL = "SELECT * FROM subtask WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int assigneeId = rs.getInt("assignee_id");
                // Retrieve the user information based on the userId
                user = getUserByID(assigneeId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get assigned user for subtask", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public List<User> getAllTaskAssignees(int taskId) {
        Connection con = null;
        List<User> users = new ArrayList<>();
        try{
            con = dbManager.getConnection();
            String SQL = "SELECT user_id FROM task_assignee WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = getUserByID(userId);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get all task assignees", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }
}
