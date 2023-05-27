package dk.kea.projectmanagement.repository;


import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SubtaskRepositoryTest {
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
    public void createSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet generatedKeysMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(generatedKeysMock);
        when(generatedKeysMock.next()).thenReturn(true); // Indicate that a key was generated
        when(generatedKeysMock.getInt(1)).thenReturn(1); // Return the generated key

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        Subtask form = new Subtask("Subtask Title", "Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 100.0, "todo");
        int taskId = 1;
        int projectId = 1;

        Subtask result = subtaskRepository.createSubtask(form, taskId, projectId);

        assertEquals(1, result.getId());
        assertEquals("Subtask Title", result.getTitle());
        assertEquals("Subtask Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), result.getEndDate());
        assertEquals(100.0, result.getCost(), 0.01);
        assertEquals(taskId, result.getTaskId());
        assertEquals(projectId, result.getProjectId());
        assertEquals(0, result.getPercentageCompletion(), 0.01);

        verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatementMock).setString(1, "Subtask Title");
        verify(preparedStatementMock).setString(2, "Subtask Description");
        verify(preparedStatementMock).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setDate(4, Date.valueOf(LocalDate.now().plusDays(7)));
        verify(preparedStatementMock).setDouble(5, 100.0);
        verify(preparedStatementMock).setInt(6, taskId);
        verify(preparedStatementMock).setInt(7, projectId);
        verify(preparedStatementMock).setDouble(8, 0);
        verify(preparedStatementMock).executeUpdate();
        verify(generatedKeysMock).next();
    }

    @Test
    public void createSubtaskTest_SQLException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet generatedKeysMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(new SQLException("SQL exception occurred")); // Simulate a SQL exception

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        Subtask form = new Subtask("Subtask Title", "Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 100.0, "todo");
        int taskId = 1;
        int projectId = 1;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subtaskRepository.createSubtask(form, taskId, projectId);
        });

        assertTrue(exception.getCause() instanceof SQLException);
        assertTrue(exception.getCause().getMessage().contains("SQL exception occurred"));

        verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatementMock).setString(1, "Subtask Title");
        verify(preparedStatementMock).setString(2, "Subtask Description");
        verify(preparedStatementMock).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setDate(4, Date.valueOf(LocalDate.now().plusDays(7)));
        verify(preparedStatementMock).setDouble(5, 100.0);
        verify(preparedStatementMock).setInt(6, taskId);
        verify(preparedStatementMock).setInt(7, projectId);
        verify(preparedStatementMock).setDouble(8, 0);
        verify(preparedStatementMock).executeUpdate();
        verify(generatedKeysMock, times(0)).next();
    }


    @Test
    public void getSubtasksByTaskIdTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Indicate that a record was retrieved and then end of record set

        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("Subtask Title");
        when(resultSetMock.getString("description")).thenReturn("Subtask Description");
        when(resultSetMock.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(7)));
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getInt("task_id")).thenReturn(1);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(0.0);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;

        List<Subtask> results = subtaskRepository.getSubtasksByTaskId(taskId);

        assertFalse(results.isEmpty());
        Subtask result = results.get(0);
        assertEquals(1, result.getId());
        assertEquals("Subtask Title", result.getTitle());
        assertEquals("Subtask Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), result.getEndDate());
        assertEquals(100.0, result.getCost(), 0.01);
        assertEquals(taskId, result.getTaskId());
        assertEquals(1, result.getProjectId());
        assertEquals(0, result.getPercentageCompletion(), 0.01);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void getSubtasksByTaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(new SQLException("SQLException"));

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;

        Exception exception = assertThrows(RuntimeException.class, () -> subtaskRepository.getSubtasksByTaskId(taskId));
        assertEquals("Could not get subtasks by task id", exception.getMessage());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeQuery();
    }


    @Test
    public void getSubtaskByTaskIdAndSubtaskIdTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true); // Indicate that a subtask was retrieved

        when(resultSetMock.getString("title")).thenReturn("Subtask Title");
        when(resultSetMock.getString("description")).thenReturn("Subtask Description");
        when(resultSetMock.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(7)));
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(0.0);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;
        int subtaskId = 1;

        Subtask result = subtaskRepository.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);

        assertNotNull(result);
        assertEquals("Subtask Title", result.getTitle());
        assertEquals("Subtask Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), result.getEndDate());
        assertEquals(100.0, result.getCost(), 0.01);
        assertEquals(taskId, result.getTaskId());
        assertEquals(subtaskId, result.getId());
        assertEquals(1, result.getProjectId());
        assertEquals(0, result.getPercentageCompletion(), 0.01);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void getSubtaskByTaskIdAndSubtaskIdNegativeTest() throws SQLException {
        // We mock Connection, PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(new SQLException("SQLException"));

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the getSubtaskByTaskIdAndSubtaskId method
        int taskId = 1;
        int subtaskId = 1;

        // Call the method under test and expect a RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> subtaskRepository.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId));
        assertEquals("Could not get subtask", exception.getMessage());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeQuery();
    }


    @Test
    public void editSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        Subtask form = new Subtask("Edited Subtask Title", "Edited Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 200.0, "done");
        int taskId = 1;
        int subtaskId = 1;

        boolean result = subtaskRepository.editSubtask(form, subtaskId, taskId);

        assertTrue(result);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "Edited Subtask Title");
        verify(preparedStatementMock).setString(2, "Edited Subtask Description");
        verify(preparedStatementMock).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setDouble(4, 200.0);
        verify(preparedStatementMock).setDate(5, Date.valueOf(LocalDate.now().plusDays(7)));
        verify(preparedStatementMock).setString(6, "done");
        verify(preparedStatementMock).setInt(7, subtaskId);
        verify(preparedStatementMock).setInt(8, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }

    @Test
    public void editSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(new SQLException("SQLException"));

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        Subtask form = new Subtask("Edited Subtask Title", "Edited Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 200.0, "done");
        int taskId = 1;
        int subtaskId = 1;

        Exception exception = assertThrows(RuntimeException.class, () -> subtaskRepository.editSubtask(form, subtaskId, taskId));
        assertEquals("Could not edit subtask", exception.getMessage());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "Edited Subtask Title");
        verify(preparedStatementMock).setString(2, "Edited Subtask Description");
        verify(preparedStatementMock).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setDouble(4, 200.0);
        verify(preparedStatementMock).setDate(5, Date.valueOf(LocalDate.now().plusDays(7)));
        verify(preparedStatementMock).setString(6, "done");
        verify(preparedStatementMock).setInt(7, subtaskId);
        verify(preparedStatementMock).setInt(8, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).rollback();
    }

    @Test
    public void updateSubtaskStatusTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;
        String subtaskStatus = "done";

        subtaskRepository.updateSubtaskStatus(taskId, subtaskStatus);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, subtaskStatus);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }


    @Test
    public void updateSubtaskStatusNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(SQLException.class); // Simulate an SQL exception

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;
        String subtaskStatus = "done";

        subtaskRepository.updateSubtaskStatus(taskId, subtaskStatus);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, subtaskStatus);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).rollback();
    }


    @Test
    public void getCommentsForSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, true, false);
        when(resultSetMock.getString("comment")).thenReturn("comment 1", "comment 2");

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        List<String> comments = subtaskRepository.getCommentsForSubtask(subtaskId);

        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("comment 1", comments.get(0));
        assertEquals("comment 2", comments.get(1));

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(3)).next();
        verify(resultSetMock, times(2)).getString("comment");  // getString should be called twice as there are 2 comments
    }


    @Test
    public void getCommentsForSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        assertThrows(RuntimeException.class, () -> subtaskRepository.getCommentsForSubtask(subtaskId));

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeQuery();
    }


    @Test
    public void addCommentToSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        String comment = "Test comment";

        subtaskRepository.addCommentToSubtask(subtaskId, comment);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, comment);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void addCommentToSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        String comment = "Test Comment";

        assertThrows(RuntimeException.class, () -> {
            subtaskRepository.addCommentToSubtask(subtaskId, comment);
        });

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, comment);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteCommentsForSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        subtaskRepository.deleteCommentsForSubtask(subtaskId);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteCommentsForSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenThrow(new SQLException());

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        assertThrows(RuntimeException.class, () -> subtaskRepository.deleteCommentsForSubtask(subtaskId));
    }

    @Test
    public void deleteSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;

        subtaskRepository.deleteSubtask(taskId);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int taskId = 1;

        assertThrows(RuntimeException.class, () -> subtaskRepository.deleteSubtask(taskId));

        verify(connectionMock).rollback();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void getSubtasksByProjectIdTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        doNothing().when(preparedStatementMock).setInt(eq(1), anyInt());
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false);

        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("Subtask 1");
        when(resultSetMock.getString("description")).thenReturn("Description 1");
        when(resultSetMock.getDate("start_date")).thenReturn(null);
        when(resultSetMock.getDate("end_date")).thenReturn(null);
        when(resultSetMock.getDouble("cost")).thenReturn(10.0);
        when(resultSetMock.getInt("task_id")).thenReturn(1);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(50.0);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int projectId = 1;

        List<Subtask> subtasks = subtaskRepository.getSubtasksByProjectId(projectId);

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());

        Subtask subtask = subtasks.get(0);
        assertEquals(1, subtask.getId());
        assertEquals("Subtask 1", subtask.getTitle());
        assertEquals("Description 1", subtask.getDescription());
        assertNull(subtask.getStartDate());
        assertNull(subtask.getEndDate());
        assertEquals(10.0, subtask.getCost());
        assertEquals(1, subtask.getTaskId());
        assertEquals(1, subtask.getProjectId());
        assertEquals(50.0, subtask.getPercentageCompletion(), 0.01);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, projectId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(2)).next();
        verify(resultSetMock).getInt("id");
        verify(resultSetMock).getString("title");
        verify(resultSetMock).getString("description");
        verify(resultSetMock).getDate("start_date");
        verify(resultSetMock).getDate("end_date");
        verify(resultSetMock).getDouble("cost");
        verify(resultSetMock).getInt("task_id");
        verify(resultSetMock).getInt("project_id");
        verify(resultSetMock).getDouble("completion_percentage");
    }

    @Test
    public void getSubtasksByProjectIdNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        doNothing().when(preparedStatementMock).setInt(eq(1), anyInt());

        when(preparedStatementMock.executeQuery()).thenThrow(new SQLException("Database connection error"));

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int projectId = 1;

        assertThrows(RuntimeException.class, () -> subtaskRepository.getSubtasksByProjectId(projectId));

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, projectId);
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void completeSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        subtaskRepository.completeSubtask(subtaskId);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setDate(1, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }


    @Test
    public void completeSubtaskNegativeTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0); // Indicate that no rows were affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;

        subtaskRepository.completeSubtask(subtaskId);
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setDate(1, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock, never()).rollback(); // no rollback, since no rows are affected
    }

    @Test
    public void addUserToSubtaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        int userId = 1;

        subtaskRepository.addUserToSubtask(subtaskId, userId);

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }

    @Test
    public void addUserToSubtaskTest_SQLException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(new SQLException("SQL exception occurred")); // Simulate a SQL exception

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        int userId = 1;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subtaskRepository.addUserToSubtask(subtaskId, userId);
        });

        assertTrue(exception.getCause() instanceof SQLException);
        assertTrue(exception.getCause().getMessage().contains("SQL exception occurred"));

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock, times(0)).commit(); // There should be no commit after an exception
    }



    @Test
    public void updatePercentagePositiveTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        DBManager dbManagerMock = mock(DBManager.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        int percentage = 75;

        subtaskRepository.updatePercentage(subtaskId, percentage);

        verify(dbManagerMock).getConnection();
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, percentage);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void updatePercentageNegativeTest_SQLException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        DBManager dbManagerMock = mock(DBManager.class);

        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(new SQLException("SQL exception occurred"));

        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        int subtaskId = 1;
        int percentage = 75;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subtaskRepository.updatePercentage(subtaskId, percentage);
        });

        assertTrue(exception.getCause() instanceof SQLException);
        assertTrue(exception.getCause().getMessage().contains("SQL exception occurred"));

        verify(dbManagerMock).getConnection();
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, percentage);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock, never()).commit(); // Ensure commit() is not invoked
        verify(connectionMock).setAutoCommit(false);
        verify(connectionMock).rollback();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }
}
