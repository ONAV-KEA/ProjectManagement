package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository("subtask_repo")
public class SubtaskRepository implements ISubtaskRepository{

    private final DBManager dbManager;

    public SubtaskRepository(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void addCommentToSubtask(int subtaskId, String comment) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public Subtask createSubtask(Subtask form, int taskId, int projectId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO subtask (title, description, start_date, end_date, cost, task_id, project_id, completion_percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getTitle());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setDouble(5, form.getCost());
            ps.setInt(6, taskId);
            ps.setInt(7, projectId);
            ps.setDouble(8, 0);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Subtask subtask = new Subtask(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getCost(), taskId, projectId, 0);
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
    public void deleteSubtask(int taskId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId) {
        Connection con = null;
        Subtask subtask = null;
        try {
            con = dbManager.getConnection();
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
                int projectId = rs.getInt("project_id");
                double percentageCompletion = rs.getDouble("completion_percentage");
                subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, taskId, projectId, percentageCompletion);
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
            con = dbManager.getConnection();
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
                int projectId = rs.getInt("project_id");
                double percentageCompletion = rs.getDouble("completion_percentage");
                Subtask subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, task_id, projectId, percentageCompletion);
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
    public boolean editSubtask(Subtask form, int subtaskId, int taskId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public void updateSubtaskStatus(int taskId, String subtaskStatus) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
    public List<String> getCommentsForSubtask(int subtaskId) {
        List<String> comments = new ArrayList<>();
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
            con = dbManager.getConnection();
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

    @Override
    public List<Subtask> getSubtasksByProjectId(int projectId) {
        Connection con = null;
        List<Subtask> subtasks = new ArrayList<>();
        try {
            con = dbManager.getConnection();
            String SQL = "SELECT * FROM subtask WHERE project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int subtaskId = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                double cost = rs.getDouble("cost");
                int task_id = rs.getInt("task_id");
                int project_id = rs.getInt("project_id");
                double completionPercentage = rs.getDouble("completion_percentage");
                Subtask subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, task_id, project_id, completionPercentage);
                subtasks.add(subtask);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtasks by project id", e);
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
    public void completeSubtask(int subtaskId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);


            String SQL = "UPDATE subtask SET completion_percentage = 100, end_date = ?, status = 'completed' WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, subtaskId);
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
            throw new RuntimeException("Could not complete subtask", e);
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
    public void addUserToSubtask(int subtaskId, int userId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);

            String SQL = "UPDATE subtask SET assignee_id = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, userId);
            ps.setInt(2, subtaskId);
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
            throw new RuntimeException("Could not add user to subtask", e);
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
    public void updatePercentage(int subtaskId, int percentage) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);

            String SQL = "UPDATE subtask SET completion_percentage = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, percentage);
            ps.setInt(2, subtaskId);
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
            throw new RuntimeException("Could not update percentage", e);
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
