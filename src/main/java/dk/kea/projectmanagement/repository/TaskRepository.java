package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository("task_repo")
public class TaskRepository implements ITaskRepository{

    private final DBManager dbManager;
    private final SubtaskRepository subtaskRepository;

    public TaskRepository(DBManager dbManager, SubtaskRepository subtaskRepository) {
        this.dbManager = dbManager;
        this.subtaskRepository = subtaskRepository;
    }


    @Override
    public List<Task> getTasksByProjectId(int projectId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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

    @Override
    public Task createTask(Task form, int projectId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public void deleteTask(int taskId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);

            // Delete associated subtasks first
            String subtaskSQL = "DELETE FROM subtask WHERE task_id = ?";
            PreparedStatement subtaskPS = con.prepareStatement(subtaskSQL);
            subtaskPS.setInt(1, taskId);
            subtaskPS.executeUpdate();

            // Delete the main task
            String taskSQL = "DELETE FROM task WHERE id = ?";
            PreparedStatement taskPS = con.prepareStatement(taskSQL);
            taskPS.setInt(1, taskId);
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
    public Task getTaskById(int taskId, int projectId) {
        Connection con = null;
        Task task = null;
        try {
            con = dbManager.getConnection();
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
    public boolean editTask(Task form, int taskId, int projectId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public void updateTaskStatus(int taskId, String status) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public void completeTask(int taskId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);


            String SQL = "UPDATE task SET completion_percentage = 100, end_date = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, taskId);
            ps.executeUpdate();

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

    @Override
    public List<String> getCommentsForTask(int taskId) {
        List<String> comments = new ArrayList<>();
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public void deleteCommentsForTask(int taskId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);


            // Delete the comments for the main task
            String SQL = "DELETE FROM comments WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ps.executeUpdate();

            con.commit();
        } catch(SQLException e){
            if(con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not delete comments", e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addCommentToTask(int taskId, String comment) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id) {
        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT " +
                    "t.id AS task_id, " +
                    "t.title AS task_title, " +
                    "t.description AS task_description, " +
                    "t.start_date AS task_start_date, " +
                    "t.end_date AS task_end_date, " +
                    "t.assignee_id AS task_assignee_id, " +
                    "t.cost AS task_cost, " +
                    "t.status AS task_status, " +
                    "t.completion_percentage AS task_completion_percentage, " +
                    "st.id AS subtask_id, " +
                    "st.title AS subtask_title, " +
                    "st.description AS subtask_description, " +
                    "st.start_date AS subtask_start_date, " +
                    "st.end_date AS subtask_end_date, " +
                    "st.assignee_id AS subtask_assignee_id, " +
                    "st.cost AS subtask_cost, " +
                    "st.status AS subtask_status, " +
                    "st.completion_percentage AS subtask_completion_percentage " +
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
                    startDate = convertToUTC(startDate);
                    LocalDate endDate = rs.getDate("task_end_date") == null ? null : rs.getDate("task_end_date").toLocalDate();
                    endDate = convertToUTC(endDate);
                    int assigneeId = rs.getInt("task_assignee_id");
                    double cost = rs.getDouble("task_cost");
                    String status = rs.getString("task_status");
                    int taskCompletionPercentage = rs.getInt("task_completion_percentage");
                    List<String> comments = getCommentsForTask(taskId);
                    task = new TaskAndSubtaskDTO(taskId, title, description, startDate, endDate, assigneeId, cost, status, comments, id, new ArrayList<>(), taskCompletionPercentage);
                    taskMap.put(taskId, task);
                } else {
                    task = taskMap.get(taskId);
                }

                int subtaskId = rs.getInt("subtask_id");
                if (subtaskId > 0) {
                    String subtaskTitle = rs.getString("subtask_title");
                    String subtaskDescription = rs.getString("subtask_description");
                    LocalDate subtaskStartDate = rs.getDate("subtask_start_date") == null ? null : rs.getDate("subtask_start_date").toLocalDate();
                    subtaskStartDate = convertToUTC(subtaskStartDate);
                    LocalDate subtaskEndDate = rs.getDate("subtask_end_date") == null ? null : rs.getDate("subtask_end_date").toLocalDate();
                    subtaskEndDate = convertToUTC(subtaskEndDate);
                    int subtaskAssigneeId = rs.getInt("subtask_assignee_id");
                    double subtaskCost = rs.getDouble("subtask_cost");
                    String subtaskStatus = rs.getString("subtask_status");
                    int subtaskCompletionPercentage = rs.getInt("subtask_completion_percentage");
                    List<String> subtaskComments = subtaskRepository.getCommentsForSubtask(subtaskId);
                    Subtask subtask = new Subtask(subtaskId, subtaskTitle, subtaskDescription, subtaskStartDate, subtaskEndDate,
                            subtaskAssigneeId, subtaskCost, subtaskStatus, subtaskComments, taskId, id, subtaskCompletionPercentage);
                    task.getSubtasks().add(subtask);
                }
            }

            return new ArrayList<>(taskMap.values());

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private LocalDate convertToUTC(LocalDate date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime zdt = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime utcZdt = zdt.withZoneSameInstant(ZoneOffset.UTC);
        return utcZdt.toLocalDate();
    }


    @Override
    public void updateTaskCostFromSubtasks(int taskId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "SELECT SUM(cost) AS total_cost FROM subtask WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                double totalCost = rs.getDouble("total_cost");
                String SQL2 = "UPDATE task SET cost = ? WHERE id = ?;";
                PreparedStatement ps2 = con.prepareStatement(SQL2);
                ps2.setDouble(1, totalCost);
                ps2.setInt(2, taskId);
                ps2.executeUpdate();
            }
            con.commit();
        } catch(SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            try{
                con.setAutoCommit(true);
                con.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void addMemberToTask(int taskId, int memberId) {
        try {
            Connection con = dbManager.getConnection();
            String sql = "INSERT INTO task_assignee (task_id, user_id) VALUES (?, ?);";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, taskId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void removeMemberFromTask(int taskId, int memberId) {
        try {
            Connection con = dbManager.getConnection();
            String sql = "DELETE FROM task_assignee WHERE task_id = ? AND user_id = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, taskId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
}
    }

    @Override
    public void addAllSubtaskAssigneesToMainTask(int taskId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            String SQL1 = "SELECT assignee_id FROM subtask WHERE task_id = ?;";
            PreparedStatement ps1 = con.prepareStatement(SQL1);
            ps1.setInt(1, taskId);
            ResultSet rs = ps1.executeQuery();

            String SQL2 = "INSERT INTO task_assignee (task_id, user_id) VALUES (?, ?);";
            PreparedStatement ps2 = con.prepareStatement(SQL2);
            ps2.setInt(1, taskId);
            while(rs.next()){
                int assigneeId = rs.getInt("assignee_id");
                ps2.setInt(2, assigneeId);
                ps2.executeUpdate();
            }
        } catch(SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            try{
                con.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateTaskCompletionPercentage(int taskId, double percentageCompletion) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            String SQL = "UPDATE task SET completion_percentage = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setDouble(1, percentageCompletion);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch(SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            try{
                con.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
}
