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

    @Override
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
                    //List<String> comments = getCommentsForTask(taskId);
                    //task = new TaskAndSubtaskDTO(taskId, title, description, startDate, endDate, assigneeId, cost, status, comments, id, new ArrayList<>());
                    //taskMap.put(taskId, task);
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
                    //task.getSubtasks().add(subtask);
                }
            }

            return new ArrayList<>(taskMap.values());

        }catch(SQLException ex){
            throw new RuntimeException(ex);
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
    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId) {
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

    @Override
    public List<Subtask> getSubtasksByTaskId(int taskId) {
        Connection con = null;
        List<Subtask> subtasks = new ArrayList<>();
        try {
            con = DBManager.getConnection();
            String SQL = "SELECT * FROM subtask WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int subtaskId = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                double cost = rs.getDouble("cost");
                int task_id = rs.getInt("task_id");
                Subtask subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, task_id);
                subtasks.add(subtask);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtasks by task id", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subtasks;
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
    public void deleteCommentsForSubtask(int subtaskId) {
        Connection con = null;
        try{
            con = DBManager.getConnection();
            con.setAutoCommit(false);

            // Delete the comments for the subtask
            String SQL = "DELETE FROM comments WHERE subtask_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);
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

}

