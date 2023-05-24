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

    @Test
    public void createSubtaskTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet generatedKeysMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(generatedKeysMock);
        when(generatedKeysMock.next()).thenReturn(true); // Indicate that a key was generated
        when(generatedKeysMock.getInt(1)).thenReturn(1); // Return the generated key

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the createSubtask method
        Subtask form = new Subtask("Subtask Title", "Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 100.0, "todo");
        int taskId = 1;
        int projectId = 1;

        // Call the method under test
        Subtask result = subtaskRepository.createSubtask(form, taskId, projectId);

        // We check to see if the subtask has the correct variables
        assertEquals(1, result.getId());
        assertEquals("Subtask Title", result.getTitle());
        assertEquals("Subtask Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getStartDate());
        assertEquals(LocalDate.now().plusDays(7), result.getEndDate());
        assertEquals(100.0, result.getCost(), 0.01);
        assertEquals(taskId, result.getTaskId());
        assertEquals(projectId, result.getProjectId());
        assertEquals(0, result.getPercentageCompletion(), 0.01);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
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
    public void getSubtasksByTaskIdTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Indicate that a record was retrieved and then end of record set

        // Mocking the resultSet data
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("Subtask Title");
        when(resultSetMock.getString("description")).thenReturn("Subtask Description");
        when(resultSetMock.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(7)));
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getInt("task_id")).thenReturn(1);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(0.0);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the getSubtasksByTaskId method
        int taskId = 1;

        // Call the method under test
        List<Subtask> results = subtaskRepository.getSubtasksByTaskId(taskId);

        // We check to see if the subtask has the correct variables
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

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void getSubtasksByTaskNegativeTest() throws SQLException {
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

        // Inputs for the getSubtasksByTaskId method
        int taskId = 1;

        // Call the method under test and expect a RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> subtaskRepository.getSubtasksByTaskId(taskId));
        assertEquals("Could not get subtasks by task id", exception.getMessage());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeQuery();
    }


    @Test
    public void getSubtaskByTaskIdAndSubtaskIdTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true); // Indicate that a record was retrieved

        // Mocking the resultSet data
        when(resultSetMock.getString("title")).thenReturn("Subtask Title");
        when(resultSetMock.getString("description")).thenReturn("Subtask Description");
        when(resultSetMock.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(7)));
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(0.0);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the getSubtaskByTaskIdAndSubtaskId method
        int taskId = 1;
        int subtaskId = 1;

        // Call the method under test
        Subtask result = subtaskRepository.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);

        // We check to see if the subtask has the correct variables
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

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
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
        // We mock Connection and PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the editSubtask method
        Subtask form = new Subtask("Edited Subtask Title", "Edited Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 200.0, "done");
        int taskId = 1;
        int subtaskId = 1;

        // Call the method under test
        boolean result = subtaskRepository.editSubtask(form, subtaskId, taskId);

        // We check to see if the subtask was successfully edited
        assertTrue(result);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
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
        // We mock Connection and PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(new SQLException("SQLException"));

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the editSubtask method
        Subtask form = new Subtask("Edited Subtask Title", "Edited Subtask Description", LocalDate.now(), LocalDate.now().plusDays(7), 200.0, "done");
        int taskId = 1;
        int subtaskId = 1;

        // Call the method under test and expect a RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> subtaskRepository.editSubtask(form, subtaskId, taskId));
        assertEquals("Could not edit subtask", exception.getMessage());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
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
        // We mock Connection and PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the updateSubtaskStatus method
        int taskId = 1;
        String subtaskStatus = "done";

        // Call the method under test
        subtaskRepository.updateSubtaskStatus(taskId, subtaskStatus);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, subtaskStatus);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }


    @Test
    public void updateSubtaskStatusNegativeTest() throws SQLException {
        // We mock Connection and PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(SQLException.class); // Simulate an SQL exception

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the updateSubtaskStatus method
        int taskId = 1;
        String subtaskStatus = "done";

        // Call the method under test
        subtaskRepository.updateSubtaskStatus(taskId, subtaskStatus);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, subtaskStatus);
        verify(preparedStatementMock).setInt(2, taskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).rollback();
    }


    @Test
    public void getCommentsForSubtaskTest() throws SQLException {
        // Mocking Connection, PreparedStatement, and ResultSet
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement executes a query, return the result set mock
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // When the result set is asked for next, return true for the first two times, and false after that to simulate two rows in the result set
        when(resultSetMock.next()).thenReturn(true, true, false);

        // When the result set is asked for a string with column name "comment", return "comment 1" for the first time and "comment 2" for the second time
        when(resultSetMock.getString("comment")).thenReturn("comment 1", "comment 2");

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the getCommentsForSubtask method
        int subtaskId = 1;

        // Call the method under test
        List<String> comments = subtaskRepository.getCommentsForSubtask(subtaskId);

        // We check to see if the two expected comments are there
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("comment 1", comments.get(0));
        assertEquals("comment 2", comments.get(1));

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(3)).next();  // it should be called 3 times according to our when statements
        verify(resultSetMock, times(2)).getString("comment");  // getString should be called twice as there are 2 comments
    }


    @Test
    public void getCommentsForSubtaskNegativeTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement executes a query, throw a SQLException
        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the getCommentsForSubtask method
        int subtaskId = 1;

        // Call the method under test
        // We expect a RuntimeException because the method we're testing should catch and throw SQLException
        assertThrows(RuntimeException.class, () -> subtaskRepository.getCommentsForSubtask(subtaskId));

        // At last, we verify to see if the right methods were called on the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeQuery();
    }


    @Test
    public void addCommentToSubtaskTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement executes an update, return 1 (indicating one row affected)
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the addCommentToSubtask method
        int subtaskId = 1;
        String comment = "Test comment";

        // Call the method under test
        subtaskRepository.addCommentToSubtask(subtaskId, comment);

        // Verify if the correct methods were called on the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, comment);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void addCommentToSubtaskNegativeTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement executes an update, return 0 to indicate no rows were affected
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the addCommentToSubtask method
        int subtaskId = 1;
        String comment = "Test Comment";

        // We expect a RuntimeException to be thrown, as no rows are affected
        assertThrows(RuntimeException.class, () -> {
            subtaskRepository.addCommentToSubtask(subtaskId, comment);
        });

        // At last, we verify to see if the right methods were called on the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, comment);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteCommentsForSubtaskTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the deleteCommentsForSubtask method
        int subtaskId = 1;

        // Call the method under test
        subtaskRepository.deleteCommentsForSubtask(subtaskId);

        // At last, we verify to see if the right methods were called on the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, subtaskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteCommentsForSubtaskNegativeTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, throw a SQLException
        when(connectionMock.prepareStatement(any(String.class))).thenThrow(new SQLException());

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the deleteCommentsForSubtask method
        int subtaskId = 1;

        // Assert that a RuntimeException is thrown when we call deleteCommentsForSubtask method
        assertThrows(RuntimeException.class, () -> subtaskRepository.deleteCommentsForSubtask(subtaskId));
    }

    @Test
    public void deleteSubtaskTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the deleteSubtask method
        int taskId = 1;

        // Call the method under test
        subtaskRepository.deleteSubtask(taskId);

        // Verify that the correct methods were called on the mock objects with the correct parameters
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, taskId);
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void deleteSubtaskNegativeTest() throws SQLException {
        // Mocking Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // Make executeUpdate() throw an SQLException
        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the deleteSubtask method
        int taskId = 1;

        // We use assertThrows to check if the RuntimeException is indeed thrown when we call deleteSubtask
        assertThrows(RuntimeException.class, () -> subtaskRepository.deleteSubtask(taskId));

        // Verify that the rollback method was called due to the SQLException
        verify(connectionMock).rollback();

        // Verify that the setAutoCommit and close methods were called on the Connection mock
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void getSubtasksByProjectIdTest() throws SQLException {
        // Mocking Connection, PreparedStatement, and ResultSet
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement sets the project ID, do nothing
        doNothing().when(preparedStatementMock).setInt(eq(1), anyInt());

        // When the prepared statement executes a query, return the result set mock
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // When the result set is asked for next, return true for the first time and false after that to simulate one row in the result set
        when(resultSetMock.next()).thenReturn(true, false);

        // When the result set is asked for values, return the expected values
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("Subtask 1");
        when(resultSetMock.getString("description")).thenReturn("Description 1");
        when(resultSetMock.getDate("start_date")).thenReturn(null);
        when(resultSetMock.getDate("end_date")).thenReturn(null);
        when(resultSetMock.getDouble("cost")).thenReturn(10.0);
        when(resultSetMock.getInt("task_id")).thenReturn(1);
        when(resultSetMock.getInt("project_id")).thenReturn(1);
        when(resultSetMock.getDouble("completion_percentage")).thenReturn(50.0);

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Input for the getSubtasksByProjectId method
        int projectId = 1;

        // Call the method under test
        List<Subtask> subtasks = subtaskRepository.getSubtasksByProjectId(projectId);

        // Check the returned subtasks
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

        // Verify the interactions with the mocked objects
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
        // Mocking Connection, PreparedStatement, and ResultSet
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // When the prepared statement sets the project ID, do nothing
        doNothing().when(preparedStatementMock).setInt(eq(1), anyInt());

        // When the prepared statement executes a query, throw SQLException to simulate a database error
        when(preparedStatementMock.executeQuery()).thenThrow(new SQLException("Database connection error"));

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // Create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Input for the getSubtasksByProjectId method
        int projectId = 1;

        // Call the method under test and expect an exception
        assertThrows(RuntimeException.class, () -> subtaskRepository.getSubtasksByProjectId(projectId));

        // Verify the interactions with the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, projectId);
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void completeSubtaskTest() throws SQLException {
        // Mocking Connection and PreparedStatement classes to be used to update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Input for the completeSubtask method
        int subtaskId = 1;

        // Call the method under test
        subtaskRepository.completeSubtask(subtaskId);

        // Verify the interactions with the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setDate(1, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }


    @Test
    public void completeSubtaskNegativeTest() throws SQLException {
        // Mocking Connection and PreparedStatement classes to be used to update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0); // Indicate that no rows were affected

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Input for the completeSubtask method
        int subtaskId = 1;

        // Call the method under test
        subtaskRepository.completeSubtask(subtaskId);

        // Verify the interactions with the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setDate(1, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock, never()).rollback(); // no rollback, since no rows are affected
    }

    @Test
    public void addUserToSubtaskTest() throws SQLException {
        // Mocking Connection and PreparedStatement classes to be used to update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected

        // We mock DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We create the subtaskRepository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the addUserToSubtask method
        int subtaskId = 1;
        int userId = 1;

        // Call the method under test
        subtaskRepository.addUserToSubtask(subtaskId, userId);

        // Verify the interactions with the mocked objects
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
    }

    @Test
    public void updatePercentagePositiveTest() throws SQLException {
        // Mocking Connection, PreparedStatement, and DBManager
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        DBManager dbManagerMock = mock(DBManager.class);

        // When the DBManager retrieves the connection, return the connection mock
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // When the connection prepares the statement, return the prepared statement mock
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);

        // Create the subtask repository with the mocked DBManager
        SubtaskRepository subtaskRepository = new SubtaskRepository(dbManagerMock);

        // Inputs for the updatePercentage method
        int subtaskId = 1;
        int percentage = 75;

        // Perform the update
        subtaskRepository.updatePercentage(subtaskId, percentage);

        // Verify the interactions with the mocked objects
        verify(dbManagerMock).getConnection();
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, percentage);
        verify(preparedStatementMock).setInt(2, subtaskId);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

/*
    @Test
    public void getSubtasksByUserIdTest() throws SQLException {

*/

}
