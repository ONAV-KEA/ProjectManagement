package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dto.ProjectFormDTO;
import dto.SubtaskFormDTO;
import dto.TaskFormDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dto.TaskAndSubtaskDTO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Repository("db_repo")
public class DBRepository implements IRepository {
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
            String SQL = "INSERT INTO task (title, description, start_date, cost, project_id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDouble(4, form.getCost());
            ps.setInt(5, projectId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Task task = new Task(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getCost(), projectId);
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
    @Override
    public Subtask createSubtask(SubtaskFormDTO form, int taskId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO subtask (title, description, start_date, cost, task_id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDouble(4, form.getCost());
            ps.setInt(5, taskId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Subtask subtask = new Subtask(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getCost(), taskId);
                    con.commit();
                    return subtask;
                } else {
                    throw new RuntimeException("Could not create subtask");
                }
            } else {
                throw new RuntimeException("Could not create subtask");
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not create subtask", e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Project getProjectById(int id) {
            try {
                Connection con = DBManager.getConnection();
                String SQL = "SELECT * FROM project WHERE id = ?;";
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                    return new Project(id, name, description, startDate, endDate);
                } else {
                    return null;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
    }

    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id){
        try{
            Connection con = DBManager.getConnection();
            String SQL = "SELECT " + "t.id AS task_id," + " t.title AS task_title, " + "t.description AS task_description, " + "t.start_date AS task_start_date, " +
                    "t.end_date AS task_end_date, " + "t.assignee_id AS task_assignee_id, " + "t.cost AS task_cost, " + "t.status AS task_status, " +
                    "t.comment AS task_comment, " + "st.id AS subtask_id, " + "st.title AS subtask_title, " + "st.description AS subtask_description, " +
                    "st.start_date AS subtask_start_date, " + "st.end_date AS subtask_end_date, " + "st.assignee_id AS subtask_assignee_id, " +
                    "st.cost AS subtask_cost, " + "st.status AS subtask_status, " + "st.comment AS subtask_comment " +
                    "FROM task t " +
                    "LEFT JOIN subtask st ON t.id = st.task_id " +
                    "WHERE t.project_id = ? " +
                    "ORDER BY t.id, st.id;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Map<Integer, TaskAndSubtaskDTO> taskMap = new LinkedHashMap<>();
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                TaskAndSubtaskDTO task;
                if (!taskMap.containsKey(taskId)) {
                    String title = rs.getString("task_title");
                    String description = rs.getString("task_description");
                    LocalDate startDate = rs.getDate("task_start_date") == null ? null : rs.getDate("task_start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("task_end_date") == null ? null : rs.getDate("task_end_date").toLocalDate();
                    int assigneeId = rs.getInt("task_assignee_id");
                    double cost = rs.getDouble("task_cost");
                    String status = rs.getString("task_status");
                    String comment = rs.getString("task_comment");
                    task = new TaskAndSubtaskDTO(taskId, title, description, startDate, endDate, assigneeId, cost, status, comment, id, new ArrayList<>());
                    taskMap.put(taskId, task);
                } else {
                    task = taskMap.get(taskId);
                }

                int subtaskId = rs.getInt("subtask_id");
                if (subtaskId > 0) {
                    String subtaskTitle = rs.getString("subtask_title");
                    String subtaskDescription = rs.getString("subtask_description");
                    LocalDate subtaskStartDate = rs.getDate("subtask_start_date") == null ? null : rs.getDate("subtask_start_date").toLocalDate();
                    LocalDate subtaskEndDate = rs.getDate("subtask_end_date") == null ? null : rs.getDate("subtask_end_date").toLocalDate();
                    int subtaskAssigneeId = rs.getInt("subtask_assignee_id");
                    double subtaskCost = rs.getDouble("subtask_cost");
                    String subtaskStatus = rs.getString("subtask_status");
                    String subtaskComment = rs.getString("subtask_comment");
                    Subtask subtask = new Subtask(subtaskId, subtaskTitle, subtaskDescription, subtaskStartDate, subtaskEndDate,
                            subtaskAssigneeId, subtaskCost, subtaskStatus, subtaskComment, taskId);
                    task.getSubtasks().add(subtask);
                }
            }

            return new ArrayList<>(taskMap.values());

        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addCommentToTask(int taskId, String comment) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE task SET comment = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, comment);
            ps.setInt(2, taskId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Could not add comment to task");
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

