package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("project_repo")
public class ProjectRepository implements IProjectRepository{

    private final DBManager dbManager;

    public ProjectRepository(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Project> getProjectByUserId(int id) {
        try (Connection con = dbManager.getConnection()) {
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
            return projects;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get projects by user id", e);
        }
    }


    @Override
    public Project createProject(Project form, User user) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO project (name, description, start_date, end_date) VALUES (?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, form.getName());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : Date.valueOf(LocalDate.now()));
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

    @Override
    public Project getProjectById(int id) {
        try (Connection con = dbManager.getConnection()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Could not get project by id", e);
        }
    }


    @Override
    public void deleteProject(int projectId, int userId) {
        Connection con = null;
        try{
            con = dbManager.getConnection();
            con.setAutoCommit(false);

            String SQL = "DELETE FROM project WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ps.executeUpdate();

            // Delete the project from the user's projects
            String SQL2 = "DELETE FROM project_user WHERE user_id = ? AND project_id = ?;";
            PreparedStatement ps2 = con.prepareStatement(SQL2);
            ps2.setInt(1, userId);
            ps2.setInt(2, projectId);
            ps2.executeUpdate();
            con.commit();
        } catch(SQLException e){
            if(con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not delete project", e);
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
    public void editProject(Project form, int projectId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);
            String SQL = "UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ? WHERE id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, form.getName());
            ps.setString(2, form.getDescription());
            ps.setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
            ps.setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
            ps.setInt(5, projectId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not edit project", e);
        }
    }


    @Override
    public List<Map<String, Object>> createGanttData(List<TaskAndSubtaskDTO> tasksAndSubtasks) {
        List<Map<String, Object>> ganttData = new ArrayList<>();

        for (TaskAndSubtaskDTO task : tasksAndSubtasks) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", String.valueOf(task.getId()));  // Convert to string
            taskData.put("name", task.getName());
            taskData.put("start", getEpochMillis(task.getStartDate()));
            taskData.put("end", getEpochMillis(task.getEndDate()));
            taskData.put("completed", task.getPercentageCompletion() / 100.0);

            ganttData.add(taskData);

            for (Subtask subtask : task.getSubtasks()) {
                Map<String, Object> subtaskData = new HashMap<>();
                subtaskData.put("id", String.valueOf(subtask.getId()));  // Convert to string
                subtaskData.put("parent", String.valueOf(task.getId()));  // Convert to string
                subtaskData.put("name", subtask.getTitle());
                subtaskData.put("start", getEpochMillis(subtask.getStartDate()));
                subtaskData.put("end", getEpochMillis(subtask.getEndDate()));
                subtaskData.put("completed", subtask.getPercentageCompletion() / 100.0);

                ganttData.add(subtaskData);
            }
        }

        return ganttData;
    }

    private Long getEpochMillis(LocalDate date) {
        if (date != null) {
            return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return null;
    }



    @Override
    public void inviteMember(int senderId, int recipientId, int projectId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);
            String SQL = "INSERT INTO invitations (project_id, sender_id, recipient_id, status) VALUES (?, ?, ?, 'pending')";
            PreparedStatement ps = con.prepareStatement(SQL);
            // Throw IllegalArgumentException if Ids are not valid
            if (projectId < 1 || senderId < 1 || recipientId < 1) {
                throw new IllegalArgumentException("Invalid Ids");
            }
            ps.setInt(1, projectId);
            ps.setInt(2, senderId);
            ps.setInt(3, recipientId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not invite member", e);
        }
    }


    @Override
    public void deleteProjectMember(int projectId, int userId) {
        try (Connection con = dbManager.getConnection()) {
            con.setAutoCommit(false);
            String SQL = "DELETE FROM project_user WHERE project_id = ? AND user_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete project member", e);
        }
    }


    @Override
    public int getTotalProjectCost(int projectId) {
        try (Connection con = dbManager.getConnection()) {
            String SQL = "SELECT SUM(cost) AS total_cost FROM task WHERE project_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_cost");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not get total project cost", e);
        }
    }

}
