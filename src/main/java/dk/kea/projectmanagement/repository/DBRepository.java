package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dto.ProjectFormDTO;
import dto.TaskFormDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Repository("database")
public class DBRepository {
    private final Logger logger = LoggerFactory.getLogger(DBRepository.class);

    public List<User> getAllUsers() {
        try {
            Connection con = DBManager.getConnection();
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
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
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

    public User getUserByID(int id) {
        try {
            Connection con = DBManager.getConnection();
            String SQL = "SELECT * FROM user WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                return new User(username, password, firstName, lastName, birthday, role);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User login(String username, String password) throws LoginSampleException {
        try {
            Connection con = DBManager.getConnection();
            String SQL = "SELECT * FROM user WHERE username = ? AND password = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String role = rs.getString("role");
                User user = new User(username, password, firstName, lastName, birthday, role);
                user.setId(id);
                return user;
            } else {
                throw new RuntimeException("Could not validate user");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Project> getProjectByUserId(int id) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "SELECT project.* " +
                    "FROM project_user " +
                    "INNER JOIN project ON project_user.project_id = project.id " +
                    "WHERE project_user.user_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            List<Project> projects = new ArrayList<>();
            while (rs.next()) {
                int projectId = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                Project project = new Project(projectId, name, description, startDate, endDate);
                projects.add(project);
            }
            con.commit();
            return projects;
            // Catch block will rollback the transaction if it fails
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Finally block will always run and commit the transaction if it was successful
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public Project createProject(ProjectFormDTO form, User user) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO project (name, description, start_date, end_date) VALUES (?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getName());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Project project = new Project(id, form.getName(), form.getDescription(), form.getStartDate(), form.getEndDate());
                    String SQL2 = "INSERT INTO project_user (project_id, user_id) VALUES (?, ?);";
                    PreparedStatement ps2 = con.prepareStatement(SQL2);
                    ps2.setInt(1, id);
                    ps2.setInt(2, user.getId());
                    int affectedRows2 = ps2.executeUpdate();
                    if (affectedRows2 == 1) {
                        con.commit();
                        return project;
                    } else {
                        throw new RuntimeException("Could not create project");
                    }
                } else {
                    throw new RuntimeException("Could not create project");
                }
            } else {
                throw new RuntimeException("Could not create project");
            }
            // Catch block will rollback the transaction if it fails
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not create project", e);
            // Finally block will always run and commit the transaction if it was successful
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Task> getTasksByProjectId(int projectId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "SELECT * FROM task WHERE project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                int assigneeId = rs.getInt("assignee_id");
                double cost = rs.getDouble("cost");
                String status = rs.getString("status");
                String comment = rs.getString("comment");
                int project_Id = rs.getInt("project_id");
                Task task = new Task(id, title, description, startDate, endDate, assigneeId, cost, status, comment, project_Id);
                tasks.add(task);
            }
            con.commit();
            return tasks;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Task createTask(TaskFormDTO form, int projectId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO task (title, description, start_date, end_date, assignee_id, cost, status, comment, project_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setInt(5, form.getAssigneeId());
            ps.setDouble(6, form.getCost());
            ps.setString(7, form.getStatus());
            ps.setString(8, form.getComment());
            ps.setInt(9, projectId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Task task = new Task(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getAssigneeId(), form.getCost(), form.getStatus(), form.getComment(), projectId);
                    con.commit();
                    return task;
                } else {
                    throw new RuntimeException("Could not create task");
                }
            } else {
                throw new RuntimeException("Could not create task");
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not create task", e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

