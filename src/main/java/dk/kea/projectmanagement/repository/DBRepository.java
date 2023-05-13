package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
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

    public Project createProject(Project form, User user) {
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

    public void deleteTask(int id) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);

            // Delete associated subtasks first
            String subtaskSQL = "DELETE FROM subtask WHERE task_id = ?";
            PreparedStatement subtaskPS = con.prepareStatement(subtaskSQL);
            subtaskPS.setInt(1, id);
            subtaskPS.executeUpdate();

            // Delete the main task
            String taskSQL = "DELETE FROM task WHERE id = ?";
            PreparedStatement taskPS = con.prepareStatement(taskSQL);
            taskPS.setInt(1, id);
            taskPS.executeUpdate();

            con.commit();
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
    }


    @Override
    public void deleteSubtask(int taskId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "DELETE FROM subtask WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateTaskStatus(int taskId, String status) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE task SET status = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateSubtaskStatus(int taskId, String subtaskStatus) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE subtask SET status = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, subtaskStatus);
            ps.setInt(2, taskId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                List<String> comments = getCommentsForTask(id);
                int project_Id = rs.getInt("project_id");
                Task task = new Task(id, title, description, startDate, endDate, assigneeId, cost, status, comments, project_Id);
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


    public Task createTask(Task form, int projectId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO task (title, description, start_date, end_date, cost, project_id) VALUES (?, ?, ?, ?, ?,?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setDouble(5, form.getCost());
            ps.setInt(6, projectId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Task task = new Task(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getCost(), projectId);
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
    public Subtask createSubtask(Subtask form, int taskId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO subtask (title, description, start_date, end_date, cost, task_id) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setDouble(5, form.getCost());
            ps.setInt(6, taskId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Subtask subtask = new Subtask(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getCost(), taskId);
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
                    "st.id AS subtask_id, " + "st.title AS subtask_title, " + "st.description AS subtask_description, " +
                    "st.start_date AS subtask_start_date, " + "st.end_date AS subtask_end_date, " + "st.assignee_id AS subtask_assignee_id, " +
                    "st.cost AS subtask_cost, " + "st.status AS subtask_status " +
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
                    List<String> comments = getCommentsForTask(taskId);
                    task = new TaskAndSubtaskDTO(taskId, title, description, startDate, endDate, assigneeId, cost, status, comments, id, new ArrayList<>());
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
                    List<String> subtaskComments = getCommentsForSubtask(subtaskId);
                    Subtask subtask = new Subtask(subtaskId, subtaskTitle, subtaskDescription, subtaskStartDate, subtaskEndDate,
                            subtaskAssigneeId, subtaskCost, subtaskStatus, subtaskComments, taskId);
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
            String SQL = "INSERT INTO comments (comment, task_id) VALUES (?, ?);";
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
            throw new RuntimeException("Could not add comment to task", e);
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
    public void addCommentToSubtask(int subtaskId, String comment) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO comments (comment, subtask_id) VALUES (?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, comment);
            ps.setInt(2, subtaskId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Could not add comment to subtask");
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
            throw new RuntimeException("Could not add comment to subtask", e);
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
    public Task getTaskById(int taskId, int projectId) {
        Connection con = null;
        Task task = null;
        try {
            con = DBManager.getConnection();
            String SQL = "SELECT * FROM task WHERE id = ? AND project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ps.setInt(2, projectId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                double cost = rs.getDouble("cost");
                task = new Task(taskId, title, description, startDate, endDate, cost, projectId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get task", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return task;
}

    @Override
    public Subtask getSubtaskById(int subtaskId, int taskId) {
        Connection con = null;
        Subtask subtask = null;
        try {
            con = DBManager.getConnection();
            String SQL = "SELECT * FROM subtask WHERE id = ? AND task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);
            ps.setInt(2, taskId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                double cost = rs.getDouble("cost");
                subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, taskId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtask", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subtask;
    }


    public boolean editTask(Task form, int taskId, int projectId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE task SET title = ?, description = ?, start_date = ?, cost = ?, end_date = ?, status = ? WHERE id = ? AND project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDouble(4, form.getCost());
            ps.setDate(5, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setString(6, form.getStatus());
            ps.setInt(7, taskId);
            ps.setInt(8, projectId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                con.commit();
                return true;
            } else {
                throw new RuntimeException("Could not edit task");
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not edit task", e);
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
    public boolean editSubtask(Task form, int subtaskId, int taskId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE subtask SET title = ?, description = ?, start_date = ?, cost = ?, end_date = ?, status = ? WHERE id = ? AND `task_id` = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDouble(4, form.getCost());
            ps.setDate(5, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setString(6, form.getStatus());
            ps.setInt(7, subtaskId);
            ps.setInt(8, taskId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                con.commit();
                return true;
            } else {
                throw new RuntimeException("Could not edit subtask");
            }
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not edit subtask", e);
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
    public void createUser(User form) {
    Connection con = null;
        try {
            con = DBManager.getConnection();
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
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void editUser(User form, int userId) {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "UPDATE user SET username = ?, password = ?, first_name = ?, last_name = ?, birthday = ?, role = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            // Use original value if forms is null
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
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> getCommentsForTask(int taskId) {
        List<String> comments = new ArrayList<>();
        Connection con = null;
        try {
            con = DBManager.getConnection();
            String SQL = "SELECT comment FROM comments WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String comment = rs.getString("comment");
                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get comments for task", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    @Override
    public List<String> getCommentsForSubtask(int subtaskId) {
        List<String> comments = new ArrayList<>();
        Connection con = null;
        try {
            con = DBManager.getConnection();
            String SQL = "SELECT comment FROM comments WHERE subtask_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String comment = rs.getString("comment");
                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get comments for subtask", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    @Override
    public void completeTask(int taskId, int subtaskId) {
        Connection con = null;
        try{
            con = DBManager.getConnection();
            con.setAutoCommit(false);

            if (subtaskId == 0) {
                // Complete the main task
                String SQL = "UPDATE task SET completion_percentage = 100, end_date = ? WHERE id = ?;";
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setInt(2, taskId);
                ps.executeUpdate();
            } else {
                // Complete the subtask
                String SQL = "UPDATE subtask SET completion_percentage = 100, end_date = ? WHERE id = ?;";
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setInt(2, subtaskId);
                ps.executeUpdate();
            }
            con.commit();
        } catch(SQLException e){
            if(con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not complete task", e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

