package dk.kea.projectmanagement.repository;


import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.repository.SubtaskRepository;
import dk.kea.projectmanagement.repository.TaskRepository;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

}
