package dk.kea.projectmanagement.repository;

import static org.junit.jupiter.api.Assertions.*;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.UserRepository;
import dk.kea.projectmanagement.repository.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectRepositoryTest {
        /*
  Our unit tests follow this template:

1. We create mock objects for `Connection`, `PreparedStatement`, and `ResultSet`

2. Then we define what the mocked object should do when called

3. DBManager is mocked

3. We create the object under test, injecting the mock objects as necessary

4. Call the method under test

5. Assert the results to see if test goes as expected

6. Finally, we verify to see if the tested method has interacted correctly with the dependencies

(In the rapport there is more detailed descriptions on how Mockito and JUnit has been used in our unit tests)

    */

    @Test
    public void getProjectByUserIdTest_Success() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Returning true for the first row, then false for second time because there are no more rows

        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("name")).thenReturn("name");
        when(resultSetMock.getString("description")).thenReturn("description");
        Date startDate = Date.valueOf(LocalDate.parse("2023-05-01"));
        Date endDate = Date.valueOf(LocalDate.parse("2023-05-22"));
        when(resultSetMock.getDate("start_date")).thenReturn(startDate);
        when(resultSetMock.getDate("end_date")).thenReturn(endDate);


        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        List<Project> projects = projectRepository.getProjectByUserId(1);

        assertEquals(1, projects.size());
        assertEquals(1, projects.get(0).getId());
        assertEquals("name", projects.get(0).getName());
        assertEquals("description", projects.get(0).getDescription());
        assertEquals(startDate.toLocalDate(), projects.get(0).getStartDate());
        assertEquals(endDate.toLocalDate(), projects.get(0).getEndDate());


        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(2)).next();
    }

    @Test
    public void getProjectByUserIdTest_NoProjects() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        User userMock = mock(User.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // No projects associated with the user

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int userId = 2;

        List<Project> projects = projectRepository.getProjectByUserId(userId);

        assertTrue(projects.isEmpty());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }

    @Test
    public void getProjectByUserIdTest_NoUser() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        User userMock = mock(User.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // User does not exist

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int userId = 3;

        List<Project> projects = projectRepository.getProjectByUserId(userId);

        assertTrue(projects.isEmpty());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }



    @Test
    public void createProjectTest_Success() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        PreparedStatement ps2 = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        User userMock = mock(User.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(ps2);
        when(ps2.executeUpdate()).thenReturn(1);
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(1);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project form = new Project();
        form.setName("Test Project");
        form.setDescription("Test project description");
        form.setStartDate(LocalDate.of(2023, 5, 1));
        form.setEndDate(LocalDate.of(2023, 5, 22));
        User user = new User();
        user.setId(1);

        Project result = projectRepository.createProject(form, user);

        assertEquals(1, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test project description", result.getDescription());
        assertEquals(LocalDate.of(2023, 5, 1), result.getStartDate());
        assertEquals(LocalDate.of(2023, 5, 22), result.getEndDate());

        verify(dbManagerMock).getConnection();
        verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatementMock).executeUpdate();
        verify(preparedStatementMock).getGeneratedKeys();
        verify(ps2).executeUpdate();
        verify(resultSetMock).next();
        verify(resultSetMock).getInt(1);
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void createProjectTest_Failure_NoGeneratedKeys() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        User userMock = mock(User.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // Simulate no generated keys being returned

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project form = new Project();
        form.setName("Test Project");
        form.setDescription("Test project description");
        form.setStartDate(LocalDate.of(2023, 5, 1));
        form.setEndDate(LocalDate.of(2023, 5, 22));
        User user = new User();
        user.setId(1);

        assertThrows(RuntimeException.class, () -> projectRepository.createProject(form, user));

        InOrder inOrder = inOrder(connectionMock, preparedStatementMock);
        inOrder.verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        inOrder.verify(preparedStatementMock).executeUpdate();
        inOrder.verify(connectionMock).setAutoCommit(true);
        inOrder.verify(connectionMock).close();
    }

    @Test
    public void createProjectTest_Failure_ExecuteUpdateThrowsSQLException() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        User userMock = mock(User.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(SQLException.class);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project form = new Project();
        form.setName("Test Project");
        form.setDescription("Test project description");
        form.setStartDate(LocalDate.of(2023, 5, 1));
        form.setEndDate(LocalDate.of(2023, 5, 22));
        User user = new User();
        user.setId(-1);

        assertThrows(RuntimeException.class, () -> projectRepository.createProject(form, user));

        verify(connectionMock).setAutoCommit(false);
        verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }





    @Test
    public void getProjectByIdTest_Success() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Returning true for the first row, then false for second time because there are no more rows

        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("name")).thenReturn("name");
        when(resultSetMock.getString("description")).thenReturn("description");
        Date startDate = Date.valueOf(LocalDate.parse("2023-05-01"));
        Date endDate = Date.valueOf(LocalDate.parse("2023-05-22"));
        when(resultSetMock.getDate("start_date")).thenReturn(startDate);
        when(resultSetMock.getDate("end_date")).thenReturn(endDate);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project project = projectRepository.getProjectById(1);

        assertEquals(1, project.getId());
        assertEquals("name", project.getName());
        assertEquals("description", project.getDescription());
        assertEquals(startDate.toLocalDate(), project.getStartDate());
        assertEquals(endDate.toLocalDate(), project.getEndDate());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(1)).next();
    }


    @Test
    public void getProjectByIdTest_InvalidProjectId() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // No rows returned

        Project project = projectRepository.getProjectById(-1);

        assertNull(project);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, -1);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }

    @Test
    public void getProjectByIdTest_ProjectNotFound() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // No rows found in the result set

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project project = projectRepository.getProjectById(2);

        assertNull(project);

        // Verifying the method invocations on the mocked objects
        InOrder inOrder = inOrder(connectionMock, preparedStatementMock, resultSetMock);
        inOrder.verify(connectionMock).prepareStatement(any(String.class));
        inOrder.verify(preparedStatementMock).executeQuery();
        inOrder.verify(resultSetMock).next();
    }

    @Test
    public void deleteProjectTest() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock1 = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(eq("DELETE FROM project WHERE id = ?;")))
                .thenReturn(preparedStatementMock1);
        when(connectionMock.prepareStatement(eq("DELETE FROM project_user WHERE user_id = ? AND project_id = ?;")))
                .thenReturn(preparedStatementMock2);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int projectId = 1;
        int userId = 2;

        projectRepository.deleteProject(projectId, userId);

        InOrder inOrder = inOrder(connectionMock, preparedStatementMock1, preparedStatementMock2);
        ((InOrder) inOrder).verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).prepareStatement(eq("DELETE FROM project WHERE id = ?;"));
        inOrder.verify(preparedStatementMock1).setInt(1, projectId);
        inOrder.verify(preparedStatementMock1).executeUpdate();
        inOrder.verify(connectionMock).prepareStatement(eq("DELETE FROM project_user WHERE user_id = ? AND project_id = ?;"));
        inOrder.verify(preparedStatementMock2).setInt(1, userId);
        inOrder.verify(preparedStatementMock2).setInt(2, projectId);
        inOrder.verify(preparedStatementMock2).executeUpdate();
        inOrder.verify(connectionMock).commit();
        inOrder.verify(connectionMock).setAutoCommit(true);
        inOrder.verify(connectionMock).close();
    }

    @Test
    public void editProjectTest() throws SQLException {
        DBManager dbManagerMock = mock(DBManager.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(eq("UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ? WHERE id = ?;")))
                .thenReturn(preparedStatementMock);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        Project form = new Project();
        form.setName("Updated Project");
        form.setDescription("Updated project description");
        form.setStartDate(LocalDate.of(2023, 5, 1));
        form.setEndDate(LocalDate.of(2023, 5, 22));
        int projectId = 1;

        projectRepository.editProject(form, projectId);

        InOrder inOrder = inOrder(connectionMock, preparedStatementMock);
        inOrder.verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).prepareStatement(eq("UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ? WHERE id = ?;"));
        inOrder.verify(preparedStatementMock).setString(1, form.getName());
        inOrder.verify(preparedStatementMock).setString(2, form.getDescription());
        inOrder.verify(preparedStatementMock).setDate(3, form.getStartDate() != null ? Date.valueOf(form.getStartDate()) : null);
        inOrder.verify(preparedStatementMock).setDate(4, form.getEndDate() != null ? Date.valueOf(form.getEndDate()) : null);
        inOrder.verify(preparedStatementMock).setInt(5, projectId);
        inOrder.verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void createGanttDataTest() {
        DBManager dbManagerMock = mock(DBManager.class);
        List<TaskAndSubtaskDTO> tasksAndSubtasks = new ArrayList<>();

        TaskAndSubtaskDTO task1 = new TaskAndSubtaskDTO();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setStartDate(LocalDate.of(2023, 5, 1));
        task1.setEndDate(LocalDate.of(2023, 5, 5));
        task1.setPercentageCompletion(50);
        task1.setStatus("completed");
        task1.setSubtasks(new ArrayList<>());
        tasksAndSubtasks.add(task1);

        TaskAndSubtaskDTO task2 = new TaskAndSubtaskDTO();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setStartDate(LocalDate.of(2023, 5, 6));
        task2.setEndDate(LocalDate.of(2023, 5, 10));
        task2.setPercentageCompletion(80);
        task2.setStatus("todo");
        task2.setSubtasks(new ArrayList<>());
        tasksAndSubtasks.add(task2);

        Subtask subtask1 = new Subtask();
        subtask1.setId(1);
        subtask1.setTitle("Subtask 1");
        subtask1.setStartDate(LocalDate.of(2023, 5, 2));
        subtask1.setEndDate(LocalDate.of(2023, 5, 4));
        subtask1.setPercentageCompletion(70);
        subtask1.setStatus("in_progress");
        task1.getSubtasks().add(subtask1);

        Subtask subtask2 = new Subtask();
        subtask2.setId(2);
        subtask2.setTitle("Subtask 2");
        subtask2.setStartDate(LocalDate.of(2023, 5, 7));
        subtask2.setEndDate(LocalDate.of(2023, 5, 9));
        subtask2.setPercentageCompletion(90);
        subtask2.setStatus("in_progress");
        task2.getSubtasks().add(subtask2);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        List<Map<String, Object>> result = projectRepository.createGanttData(tasksAndSubtasks);

        assertEquals(4, result.size());

        Map<String, Object> task1Data = result.get(0);
        assertEquals("1", task1Data.get("id"));
        assertEquals("Task 1", task1Data.get("name"));
        assertEquals(getEpochMillis(task1.getStartDate()), task1Data.get("start"));
        assertEquals(getEpochMillis(task1.getEndDate()), task1Data.get("end"));
        assertEquals(0.5, task1Data.get("completed"));

        Map<String, Object> subtask1Data = result.get(1);
        assertEquals("1", subtask1Data.get("id"));
        assertEquals("1", subtask1Data.get("parent"));
        assertEquals("Subtask 1", subtask1Data.get("name"));
        assertEquals(getEpochMillis(subtask1.getStartDate()), subtask1Data.get("start"));
        assertEquals(getEpochMillis(subtask1.getEndDate()), subtask1Data.get("end"));
        assertEquals(0.7, subtask1Data.get("completed"));

        Map<String, Object> task2Data = result.get(2);
        assertEquals("2", task2Data.get("id"));
        assertEquals("Task 2", task2Data.get("name"));
        assertEquals(getEpochMillis(task2.getStartDate()), task2Data.get("start"));
        assertEquals(getEpochMillis(task2.getEndDate()), task2Data.get("end"));
        assertEquals(0.8, task2Data.get("completed"));

        Map<String, Object> subtask2Data = result.get(3);
        assertEquals("2", subtask2Data.get("id"));
        assertEquals("2", subtask2Data.get("parent"));
        assertEquals("Subtask 2", subtask2Data.get("name"));
        assertEquals(getEpochMillis(subtask2.getStartDate()), subtask2Data.get("start"));
        assertEquals(getEpochMillis(subtask2.getEndDate()), subtask2Data.get("end"));
        assertEquals(0.9, subtask2Data.get("completed"));
    }

    private long getEpochMillis(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Test
    public void inviteMemberTest_Success() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        int senderId = 1;
        int recipientId = 2;
        int projectId = 3;

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        projectRepository.inviteMember(senderId, recipientId, projectId);

        // Verify the interactions and order of method calls
        InOrder inOrder = inOrder(connectionMock, preparedStatementMock);
        inOrder.verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).prepareStatement(eq("INSERT INTO invitations (project_id, sender_id, recipient_id, status) VALUES (?, ?, ?, 'pending')"));
        inOrder.verify(preparedStatementMock).setInt(1, projectId);
        inOrder.verify(preparedStatementMock).setInt(2, senderId);
        inOrder.verify(preparedStatementMock).setInt(3, recipientId);
        inOrder.verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void inviteMember_InvalidProjectId() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        int senderId = 1;
        int recipientId = 2;
        int projectId = -1; // Invalid projectId

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        // Expect the IllegalArgumentException to be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            projectRepository.inviteMember(senderId, recipientId, projectId);
        });

        // Verify the interactions and order of method calls
        InOrder inOrder = inOrder(connectionMock, preparedStatementMock);
        inOrder.verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).close();
    }





    @Test
    public void deleteProjectMemberTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock)
                .thenReturn(preparedStatementMock2);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // indicate that one row was affected
        when(preparedStatementMock2.executeUpdate()).thenReturn(1); // indicate that one row was affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        projectRepository.deleteProject(1, 1);

        // And since nothing will be returned (because method is void) there is no assertEquals, just verify methods at the end
        verify(connectionMock, times(2)).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1); // Verify that the prepared statement was set with the correct id
        verify(preparedStatementMock).executeUpdate();
        verify(preparedStatementMock2).setInt(1, 1); // Verify that the second prepared statement was set with the correct id
        verify(preparedStatementMock2).executeUpdate();
    }

    @Test
    public void deleteProjectMemberTest_MemberNotFound() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0); // Indicate that no rows were affected

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int projectId = 1;
        int memberId = -1; // Non-existent member ID

        projectRepository.deleteProjectMember(projectId, memberId);

        InOrder inOrder = inOrder(connectionMock, preparedStatementMock);
        inOrder.verify(connectionMock).setAutoCommit(false);
        inOrder.verify(connectionMock).prepareStatement(eq("DELETE FROM project_user WHERE project_id = ? AND user_id = ?;"));
        inOrder.verify(preparedStatementMock).setInt(1, projectId);
        inOrder.verify(preparedStatementMock).setInt(2, memberId);
        inOrder.verify(preparedStatementMock).executeUpdate();
    }


    @Test
    public void getTotalProjectCostTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // Set up the ResultSet to return a total cost value
        when(resultSetMock.next()).thenReturn(true); // Simulate that there is a result
        when(resultSetMock.getInt("total_cost")).thenReturn(1000); // Set the total cost value

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int totalCost = projectRepository.getTotalProjectCost(1);

        assertEquals(1000, totalCost);

        verify(connectionMock).prepareStatement(anyString());
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
        verify(resultSetMock).getInt("total_cost");
        verify(connectionMock).close();
    }

    @Test
    public void getTotalProjectCostTest_ProjectNotFound() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // Set up the ResultSet to indicate no result
        when(resultSetMock.next()).thenReturn(false); // Simulate no result found

        ProjectRepository projectRepository = new ProjectRepository(dbManagerMock);

        int totalCost = projectRepository.getTotalProjectCost(551);

        assertEquals(0, totalCost);

        verify(connectionMock).prepareStatement(anyString());
        verify(preparedStatementMock).setInt(1, 551);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
        verify(connectionMock).close();
    }
}