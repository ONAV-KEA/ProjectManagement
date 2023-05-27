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

public class TaskRepositoryTest {

    @Test
    public void getTasksByProjectIdTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false);// Simulate one row in the result set
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("title");
        when(resultSetMock.getString("description")).thenReturn("description");
        when(resultSetMock.getDate("start_date")).thenReturn(null);
        when(resultSetMock.getDate("end_date")).thenReturn(null);
        when(resultSetMock.getInt("assignee_id")).thenReturn(1);
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getString("status")).thenReturn("status");
        when(resultSetMock.getInt("project_id")).thenReturn(1);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        //We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        List<Task> tasks = taskRepository.getTasksByProjectId(1);

        // We check to see if it has found the tasks by project id
        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(1, task.getAssigneeId());
        assertEquals(100.0, task.getCost(), 0.01);
        assertEquals("status", task.getStatus());
        assertEquals(1, task.getProjectId());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(2)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(2)).executeQuery();
        verify(resultSetMock, times(3)).next();
    }

    @Test
    public void createTaskTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class), anyInt())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(1);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Create a Task object to be inserted
        Task taskForm = new Task(0, "title", "description", null, null, 100.0, 1);

        // Call the method under test
        Task task = taskRepository.createTask(taskForm, 1);

        // We check to see if it has created the task correctly
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(100.0, task.getCost(), 0.01);
        assertEquals(1, task.getProjectId());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class), anyInt());
        verify(preparedStatementMock, times(1)).executeUpdate();
        verify(preparedStatementMock, times(1)).getGeneratedKeys();
        verify(resultSetMock, times(1)).next();
    }

    @Test
    public void createTaskTest_sqlException() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class), anyInt())).thenReturn(preparedStatementMock);
        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Create a Task object to be inserted
        Task taskForm = new Task(0, "title", "description", null, null, 100.0, 1);

        // Call the method under test and assert that an SQLException is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> taskRepository.createTask(taskForm, 1));
        assertTrue(exception.getCause() instanceof SQLException);


        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class), anyInt());
        verify(preparedStatementMock, times(1)).executeUpdate();
        verify(preparedStatementMock, never()).getGeneratedKeys();
        verify(resultSetMock, never()).next();
    }


    @Test
    public void getTaskByIdTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true); // Simulate one row in the result set

        // And then we set up the results to get the data that we want to get from the mocked database
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("title")).thenReturn("title");
        when(resultSetMock.getString("description")).thenReturn("description");
        when(resultSetMock.getDate("start_date")).thenReturn(null);
        when(resultSetMock.getDate("end_date")).thenReturn(null);
        when(resultSetMock.getDouble("cost")).thenReturn(100.0);
        when(resultSetMock.getInt("project_id")).thenReturn(1);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        Task task = taskRepository.getTaskById(1, 1);

        // We check to see if it has found the task by id correctly
        assertNotNull(task);
        assertEquals("title", task.getTitle());
        assertEquals("description", task.getDescription());
        assertEquals(100.0, task.getCost(), 0.01);
        assertEquals(1, task.getProjectId());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeQuery();
        verify(resultSetMock, times(1)).next();
    }


    @Test
    public void editTaskTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Create a Task form to use as input
        Task form = new Task(1, "title", "description", null, null, 100.0, 1);

        // Call the method under test
        boolean result = taskRepository.editTask(form, 1, 1);

        // We check to see if it has edited the task correctly
        assertTrue(result);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeUpdate();
    }

    @Test
    public void editTaskTest_sqlException() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Create a Task form to use as input
        Task form = new Task(1, "title", "description", null, null, 100.0, 1);

        // Call the method under test and assert that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> taskRepository.editTask(form, 1, 1));

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeUpdate();
    }


    @Test
    public void updateTaskStatusTest() throws SQLException {
        // We mock Connection and PreparedStatement classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        // Do nothing when preparedStatementMock.executeUpdate() is called

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.updateTaskStatus(1, "completed");

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeUpdate();
        verify(preparedStatementMock, times(1)).setString(1, "completed");
        verify(preparedStatementMock, times(1)).setInt(2, 1);
    }

    @Test
    public void updateTaskCompletionPercentageTest() throws SQLException {
        // We mock Connection and PreparedStatement classes to be used to update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // Define what should happen when the mocked methods are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        // Do nothing when preparedStatementMock.executeUpdate() is called

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.updateTaskCompletionPercentage(1, 50.0);

        // Verify if the right methods were called on the mocked objects, to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeUpdate();
        verify(preparedStatementMock, times(1)).setDouble(1, 50.0);
        verify(preparedStatementMock, times(1)).setInt(2, 1);
    }


    @Test
    public void completeTaskTest() throws SQLException {
        // We mock Connection and PreparedStatement classes to be used to update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // Define what should happen when the mocked methods are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        // Do nothing when preparedStatementMock.executeUpdate() is called

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.completeTask(1);

        // Verify if the right methods were called on the mocked objects, to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeUpdate();
        verify(preparedStatementMock, times(1)).setDate(1, Date.valueOf(LocalDate.now()));
        verify(preparedStatementMock, times(1)).setInt(2, 1);
    }


    @Test
    public void updateTaskCostFromSubtasksTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve and update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock1 = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // Define what should happen when the mocked methods are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock1, preparedStatementMock2);
        when(preparedStatementMock1.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getDouble("total_cost")).thenReturn(100.0);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.updateTaskCostFromSubtasks(1);

        // Verify if the right methods were called on the mocked objects, to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(2)).prepareStatement(any(String.class));
        verify(preparedStatementMock1, times(1)).executeQuery();
        verify(resultSetMock, times(1)).next();
        verify(resultSetMock, times(1)).getDouble("total_cost");
        verify(preparedStatementMock2, times(1)).setDouble(1, 100.0);
        verify(preparedStatementMock2, times(1)).setInt(2, 1);
        verify(preparedStatementMock2, times(1)).executeUpdate();
    }

    @Test
    public void addAllSubtaskAssigneesToMainTaskTest() throws SQLException {
        // We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve and update data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock1 = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // Define what should happen when the mocked methods are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock1, preparedStatementMock2);
        when(preparedStatementMock1.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Simulate one row in the result set
        when(resultSetMock.getInt("assignee_id")).thenReturn(1);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.addAllSubtaskAssigneesToMainTask(1);

        // Verify if the right methods were called on the mocked objects, to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(2)).prepareStatement(any(String.class));
        verify(preparedStatementMock1, times(1)).executeQuery();
        verify(resultSetMock, times(2)).next();
        verify(resultSetMock, times(1)).getInt("assignee_id");
        verify(preparedStatementMock2, times(1)).setInt(1, 1);
        verify(preparedStatementMock2, times(1)).setInt(2, 1);
        verify(preparedStatementMock2, times(1)).executeUpdate();
    }

    @Test
    public void getTasksWithSubtasksByProjectIdTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false, false); // Simulate one row in the result set

        // Mocked values for TaskAndSubtaskDTO
        when(resultSetMock.getInt("task_id")).thenReturn(1);
        when(resultSetMock.getString("task_title")).thenReturn("Task title");
        when(resultSetMock.getString("task_description")).thenReturn("Task description");
        when(resultSetMock.getDate("task_start_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("task_end_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(resultSetMock.getInt("task_assignee_id")).thenReturn(1);
        when(resultSetMock.getDouble("task_cost")).thenReturn(100.00);
        when(resultSetMock.getString("task_status")).thenReturn("todo");
        when(resultSetMock.getInt("task_completion_percentage")).thenReturn(50);

        // Mocked values for Subtask
        when(resultSetMock.getInt("subtask_id")).thenReturn(2);
        when(resultSetMock.getString("subtask_title")).thenReturn("Subtask title");
        when(resultSetMock.getString("subtask_description")).thenReturn("Subtask description");
        when(resultSetMock.getDate("subtask_start_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(resultSetMock.getDate("subtask_end_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(resultSetMock.getInt("subtask_assignee_id")).thenReturn(1);
        when(resultSetMock.getDouble("subtask_cost")).thenReturn(50.00);
        when(resultSetMock.getString("subtask_status")).thenReturn("Status");
        when(resultSetMock.getInt("subtask_completion_percentage")).thenReturn(75);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);
        when(subtaskRepositoryMock.getCommentsForSubtask(anyInt())).thenReturn(Arrays.asList("Comment1", "Comment2"));

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        List<TaskAndSubtaskDTO> result = taskRepository.getTasksWithSubtasksByProjectId(1);

        // Check the results
        assertEquals(1, result.size());
        TaskAndSubtaskDTO taskAndSubtaskDTO = result.get(0);
        assertEquals("Task title", taskAndSubtaskDTO.getName());
        assertEquals("Task description", taskAndSubtaskDTO.getDescription());
        assertEquals("To Do", taskAndSubtaskDTO.getStatus());

        // Check the Subtasks
        List<Subtask> subtasks = taskAndSubtaskDTO.getSubtasks();
        assertEquals(1, subtasks.size());
        Subtask subtask = subtasks.get(0);
        assertEquals("Subtask title", subtask.getTitle());
        assertEquals("Subtask description", subtask.getDescription());
        assertEquals("Status", subtask.getStatus());
        assertEquals(1, subtask.getAssigneeId());

        // Verify the calls
        verify(connectionMock, times(2)).prepareStatement(any(String.class)); // Changed times(1) to times(2)
        verify(preparedStatementMock, times(2)).executeQuery(); // Changed times(1) to times(2)
        verify(resultSetMock, times(3)).next(); // Changed times(2) to times(3)
    }


    @Test
    public void getCommentsForTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, true, false); // Simulate two rows in the result set

        // Mocked values for comments
        when(resultSetMock.getString("comment")).thenReturn("Comment1", "Comment2");

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        List<String> result = taskRepository.getCommentsForTask(1);

        // We check to see if the comments are gotten
        assertEquals(2, result.size());
        assertEquals("Comment1", result.get(0));
        assertEquals("Comment2", result.get(1));

        // Verify the calls
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(3)).next(); // Changed times(2) to times(3)
        verify(resultSetMock, times(2)).getString("comment");
        verify(connectionMock).close();
    }

    @Test
    public void getTasksWithSubtasksByProjectIdTest_sqlException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        doThrow(new SQLException()).when(preparedStatementMock).executeQuery();

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test and assert that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> taskRepository.getTasksWithSubtasksByProjectId(1));

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock, times(1)).prepareStatement(any(String.class));
        verify(preparedStatementMock, times(1)).executeQuery();
    }


    @Test
    public void addCommentToTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row


        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.addCommentToTask(1, "New comment");

        // Verify the calls
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "New comment");
        verify(preparedStatementMock).setInt(2, 1);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).setAutoCommit(false);
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void addCommentToTaskTest_sqlException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test and assert that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> taskRepository.addCommentToTask(1, "New comment"));

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "New comment");
        verify(preparedStatementMock).setInt(2, 1);
        verify(preparedStatementMock).executeUpdate();
    }


    @Test
    public void deleteCommentsForTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.deleteCommentsForTask(1);

        // Verify the calls
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).setAutoCommit(false);
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void deleteCommentsForTaskTest_sqlException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        doThrow(new SQLException()).when(preparedStatementMock).executeUpdate();

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager and SubtaskRepository
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test and assert that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> taskRepository.deleteCommentsForTask(1));

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).executeUpdate();
    }


    @Test
    public void deleteTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement subtaskPreparedStatementMock = mock(PreparedStatement.class);
        PreparedStatement taskPreparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(subtaskPreparedStatementMock, taskPreparedStatementMock);
        when(subtaskPreparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row for subtask deletion
        when(taskPreparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row for task deletion

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);


        // Call the method under test
        taskRepository.deleteTask(1);

        // Verify the calls
        verify(connectionMock, times(2)).prepareStatement(any(String.class));
        verify(subtaskPreparedStatementMock).setInt(1, 1);
        verify(subtaskPreparedStatementMock).executeUpdate();
        verify(taskPreparedStatementMock).setInt(1, 1);
        verify(taskPreparedStatementMock).executeUpdate();
        verify(connectionMock).setAutoCommit(false);
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void deleteTaskTest_sqlException() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement subtaskPreparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(subtaskPreparedStatementMock);
        doThrow(new SQLException()).when(subtaskPreparedStatementMock).executeUpdate(); // Simulate an SQLException

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.deleteTask(1);

        // Verify the calls
        verify(connectionMock, times(1)).prepareStatement(any(String.class)); // Changed times(2) to times(1)
        verify(subtaskPreparedStatementMock).setInt(1, 1);
        verify(subtaskPreparedStatementMock).executeUpdate();
        verify(connectionMock).rollback(); // Verify that a rollback was performed
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }


    @Test
    public void addMemberToTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);


        // Call the method under test
        taskRepository.addMemberToTask(1, 1);

        // Verify the calls
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).setInt(2, 1);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).close();
    }

    @Test
    public void removeMemberFromTaskTest() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Simulate one affected row

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        // We mock the SubtaskRepository
        SubtaskRepository subtaskRepositoryMock = mock(SubtaskRepository.class);

        // We create the TaskRepository with the mocked DBManager
        TaskRepository taskRepository = new TaskRepository(dbManagerMock, subtaskRepositoryMock);

        // Call the method under test
        taskRepository.removeMemberFromTask(1, 1);

        // Verify the calls
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).setInt(2, 1);
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).close();
    }


}

