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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository("project_repo")
public class ProjectRepository implements IProjectRepository{

    private final DBManager dbManager;

    public ProjectRepository(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Project> getProjectByUserId(int id) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
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

    @Override
    public Project getProjectById(int id) {
        try {
            Connection con = dbManager.getConnection();
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
        Connection con = null;
        try {
            con = dbManager.getConnection();
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
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Could not edit project", e);
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
    public List<List<Object>> createGanttData(List<TaskAndSubtaskDTO> tasks) {
        //List to hold task and subtask data
        List<List<Object>> ganttData = new ArrayList<>();

        // id that will increment throughout this method, so every task and subtask have different ids
        int id = 1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // In the loops, task and subtask data is added (in order of what the google charts api needs,
        //starting wih task id, task name and so on
        for (TaskAndSubtaskDTO task : tasks) {
            List<Object> taskData = new ArrayList<>();
            taskData.add(String.valueOf(id)); // Task ID
            taskData.add(task.getName()); // Task Name

            LocalDate localStartDate = task.getStartDate();
            LocalDate localEndDate = task.getEndDate();

            if (localStartDate != null && localEndDate != null) {
                taskData.add(localStartDate.format(formatter)); // Start Date
                taskData.add(localEndDate.format(formatter)); // End Date
            } else {
                // We add null placeholders, as they are updated later if data is available
                taskData.add(null);
                taskData.add(null);
            }

            taskData.add(0); // Percent Complete
            taskData.add(null); // Placeholder for duration
            taskData.add(null); // Dependencies

            ganttData.add(taskData);
            int taskId = id;

            for (Subtask subtask : task.getSubtasks()) {
                id++;
                List<Object> subtaskData = new ArrayList<>();
                subtaskData.add(String.valueOf(id)); // Subtask ID
                subtaskData.add(subtask.getTitle()); // Subtask Name

                LocalDate localSubtaskStartDate = subtask.getStartDate();
                LocalDate localSubtaskEndDate = subtask.getEndDate();

                if (localSubtaskStartDate != null && localSubtaskEndDate != null) {
                    java.util.Date utilSubtaskStartDate = Date.from(localSubtaskStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    java.sql.Date subtaskStartDate = new java.sql.Date(utilSubtaskStartDate.getTime());
                    subtaskData.add(subtaskStartDate);

                    java.util.Date utilSubtaskEndDate = Date.from(localSubtaskEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    java.sql.Date subtaskEndDate = new java.sql.Date(utilSubtaskEndDate.getTime());
                    subtaskData.add(subtaskEndDate);

                    // Calculate subtask duration in milliseconds
                    if (localSubtaskStartDate != null && localSubtaskEndDate != null) {
                        long subtaskDuration = ChronoUnit.MILLIS.between(localSubtaskStartDate.atStartOfDay(), localSubtaskEndDate.atStartOfDay());
                        subtaskData.add(subtaskDuration);
                    } else {
                        subtaskData.add(null); // Add null if start or end date is null
                    }

                    // Update task start and end dates based on subtask dates
                    if (localSubtaskStartDate != null && (localStartDate == null || localSubtaskStartDate.isBefore(localStartDate))) {
                        localStartDate = localSubtaskStartDate;
                    }
                    if (localSubtaskEndDate != null && (localEndDate == null || localSubtaskEndDate.isAfter(localEndDate))) {
                        localEndDate = localSubtaskEndDate;
                    }
                }

                subtaskData.add(0); // Percent Complete
                subtaskData.add(String.valueOf(taskId)); // Add dependency to parent task

                ganttData.add(subtaskData);
            }

            // Task and subtask duration calculated because google charts api need it to be in milliseconds
            if (localStartDate != task.getStartDate() || localEndDate != task.getEndDate()) {
                java.util.Date utilStartDate = Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                java.sql.Date startDate = new java.sql.Date(utilStartDate.getTime());
                taskData.set(2, startDate);  // Update task start date

                java.util.Date utilEndDate = Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                java.sql.Date endDate = new java.sql.Date(utilEndDate.getTime());
                taskData.set(3, endDate);  // Update task end date

                // Calculate new task duration in milliseconds
                if (localStartDate != null && localEndDate != null) {
                    long duration = ChronoUnit.MILLIS.between(localStartDate.atStartOfDay(), localEndDate.atStartOfDay());
                    taskData.set(4, duration);  // Update task duration
                } else {
                    taskData.set(4, null); // Set duration as null if start or end date is null
                }
            }
            id++;
        }
        return ganttData;
    }


    @Override
    public void inviteMember(int senderId, int recipientId, int projectId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "INSERT INTO invitations (project_id, sender_id, recipient_id, status) VALUES (?, ?, ?, 'pending')";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ps.setInt(2, senderId);
            ps.setInt(3, recipientId);
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
            throw new RuntimeException("Could not invite member", e);
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
    public void deleteProjectMember(int projectId, int userId) {
        Connection con = null;
        try {
            con = dbManager.getConnection();
            con.setAutoCommit(false);
            String SQL = "DELETE FROM project_user WHERE project_id = ? AND user_id = ?;";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
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
            throw new RuntimeException("Could not delete project member", e);
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
