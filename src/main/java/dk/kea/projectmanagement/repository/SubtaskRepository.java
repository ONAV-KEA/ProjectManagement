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
        try (Connection con = dbManager.getConnection()) {
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
            throw new RuntimeException("Could not add comment to subtask", e);
        }
    }


    @Override
    public Subtask createSubtask(Subtask form, int taskId, int projectId) {
        try (Connection con = dbManager.getConnection()) {
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
            throw new RuntimeException("Could not create subtask", e);
        }
    }


    @Override
    public void deleteSubtask(int taskId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);
            String SQL = "DELETE FROM subtask WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, taskId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete subtask", e);
        }
    }


    @Override
    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId) {
        try (Connection con = dbManager.getConnection()) {
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
                return new Subtask(subtaskId, title, description, startDate, endDate, cost, taskId, projectId, percentageCompletion);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtask", e);
        }
        return null;
    }


    @Override
    public List<Subtask> getSubtasksByTaskId(int taskId) {
        List<Subtask> subtasks = new ArrayList<>();
        try (Connection con = dbManager.getConnection()) {
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
        }
        return subtasks;
    }


    @Override
    public boolean editSubtask(Subtask form, int subtaskId, int taskId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);
            String SQL = "UPDATE subtask SET title = ?, description = ?, start_date = ?, cost = ?, end_date = ?, status = ? WHERE id = ? AND task_id = ?;";
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
            throw new RuntimeException("Could not edit subtask", e);
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
            // set subtask start date to current date if status is changed to "In progress"
            if (subtaskStatus.equals("in_progress")) {
                String SQL2 = "UPDATE subtask SET start_date = ? WHERE id = ?";
                PreparedStatement ps2 = con.prepareStatement(SQL2);
                ps2.setDate(1, Date.valueOf(LocalDate.now()));
                ps2.setInt(2, taskId);
                ps2.executeUpdate();
            }
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
        try (Connection con = dbManager.getConnection()) {
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
        }
        return comments;
    }


    @Override
    public void deleteCommentsForSubtask(int subtaskId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);

            // Delete the comments for the subtask
            String SQL = "DELETE FROM comments WHERE subtask_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);
            ps.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete comments", e);
        }
    }


    @Override
    public List<Subtask> getSubtasksByProjectId(int projectId) {
        try (Connection con = dbManager.getConnection()) {
            List<Subtask> subtasks = new ArrayList<>();
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
                double completionPercentage = rs.getDouble("completion_percentage");
                Subtask subtask = new Subtask(subtaskId, title, description, startDate, endDate, cost, task_id, projectId, completionPercentage);
                subtasks.add(subtask);
            }
            return subtasks;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtasks by project id", e);
        }
    }


    @Override
    public void completeSubtask(int subtaskId) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "UPDATE subtask SET completion_percentage = 100, end_date = ?, status = 'completed' WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, subtaskId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows != 1) {
                throw new RuntimeException("Could not complete subtask");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not complete subtask", e);
        }
    }


    @Override
    public void addUserToSubtask(int subtaskId, int userId) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "UPDATE subtask SET assignee_id = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, userId);
            ps.setInt(2, subtaskId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows != 1) {
                throw new RuntimeException("Could not add user to subtask");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not add user to subtask", e);
        }
    }


    @Override
    public void updatePercentage(int subtaskId, int percentage) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "UPDATE subtask SET completion_percentage = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, percentage);
            ps.setInt(2, subtaskId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows != 1) {
                throw new RuntimeException("Could not update percentage");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update percentage", e);
        }
    }


    @Override
    public List<Subtask> getSubtasksByUserId(int userId) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "SELECT * FROM subtask WHERE assignee_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            List<Subtask> subtasks = new ArrayList<>();
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
            return subtasks;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtasks by user id", e);
        }
    }


    @Override
    public Subtask getSubtaskById(int subtaskId) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "SELECT * FROM subtask WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, subtaskId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate();
                double cost = rs.getDouble("cost");
                int task_id = rs.getInt("task_id");
                int project_id = rs.getInt("project_id");
                double completionPercentage = rs.getDouble("completion_percentage");
                return new Subtask(id, title, description, startDate, endDate, cost, task_id, project_id, completionPercentage);
            }
            return null; // Return null if no subtask is found with the specified ID
        } catch (SQLException e) {
            throw new RuntimeException("Could not get subtask by id", e);
        }
    }

}
