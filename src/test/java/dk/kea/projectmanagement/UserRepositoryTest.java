package dk.kea.projectmanagement;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.UserRepository;
import dk.kea.projectmanagement.repository.utility.DBManager;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    @Test
    public void getAllUsersTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Returning true for the first row, then false for second time because there are no more rows

        // And then we set up the results to get the data that we want to get from the mocked database
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("username")).thenReturn("username");
        when(resultSetMock.getString("password")).thenReturn("password");
        when(resultSetMock.getString("first_name")).thenReturn("firstname");
        when(resultSetMock.getString("last_name")).thenReturn("lastname");
        when(resultSetMock.getDate("birthday")).thenReturn(null);
        when(resultSetMock.getString("role")).thenReturn("role");

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        List<User> users = userRepository.getAllUsers();

        // And then see if the results are correct, according to what we set in the resultSetMock methods
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("firstname", user.getFirstName());
        assertEquals("lastname", user.getLastName());
        assertEquals(null, user.getBirthday());
        assertEquals("role", user.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(2)).next();
    }

    @Test
    public void getUserByIDTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);

        // And then we set up the results to get the data that we want to get from the mocked database
        when(resultSetMock.getString("username")).thenReturn("username");
        when(resultSetMock.getString("password")).thenReturn("password");
        when(resultSetMock.getString("first_name")).thenReturn("firstname");
        when(resultSetMock.getString("last_name")).thenReturn("lastname");
        when(resultSetMock.getDate("birthday")).thenReturn(null);
        when(resultSetMock.getString("role")).thenReturn("role");

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        User user = userRepository.getUserByID(1);

        // We check the results to see if the user data is correct
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("firstname", user.getFirstName());
        assertEquals("lastname", user.getLastName());
        assertEquals(null, user.getBirthday());
        assertEquals("role", user.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }

    @Test
    public void getUserByIDNotFoundTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // Return false which indicates no user was found

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        User user = userRepository.getUserByID(1);

        // We check to see if we get a null
        assertNull(user);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }

    @Test
    public void createUserTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet generatedKeysMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // Indicate that one row was affected
        when(preparedStatementMock.getGeneratedKeys()).thenReturn(generatedKeysMock);
        when(generatedKeysMock.next()).thenReturn(true); // Indicate that a key was generated
        when(generatedKeysMock.getInt(1)).thenReturn(1); // Return the generated key

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Prepare the user to be created
        User user = new User("username", "password", "firstname", "lastname", null, "role");

        // Call the method under test
        userRepository.createUser(user);

        // We check if user has had ID set correctly
        assertEquals(1, user.getId());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS));
        verify(preparedStatementMock).executeUpdate();
        verify(generatedKeysMock).next();
    }

    @Test
    public void editUserTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // indicate that one row was affected

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // We create a "spy" which is possible with Mockito. It does so that we can call real methods from the
        // repository but also stub at the same time (which we need because we have to use getUserByID)
        UserRepository userRepositorySpy = spy(userRepository);

        LocalDate birthday = LocalDate.of(2000, 1, 1);
        User existingUser = new User("existingUsername", "existingPassword", "existingFirstName", "existingLastName", birthday, "existingRole");
        existingUser.setId(1);
        doReturn(existingUser).when(userRepositorySpy).getUserByID(1);

        // We prepare a user to be edited
        User user = new User("newUsername", "newPassword", "newFirstName", "newLastName", null, "newRole");

        // Call the method under test
        User editedUser = userRepositorySpy.editUser(user, 1);

        // We check to see if the user has been edited
        assertEquals("newUsername", editedUser.getUsername());
        assertEquals("newPassword", editedUser.getPassword());
        assertEquals("newFirstName", editedUser.getFirstName());
        assertEquals("newLastName", editedUser.getLastName());
        assertEquals("newRole", editedUser.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeUpdate();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void deleteUserTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1); // indicate that one row was affected

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // We call the method
        userRepository.deleteUser(1);
        // And since nothing will be returned (because method is void) there is no assertEquals, just verify methods at the end

        /// At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, 1); // Verify that the prepared statement was set with the correct id
        verify(preparedStatementMock).executeUpdate();
    }

    @Test
    public void loginTest() throws SQLException, LoginSampleException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true); // Indicate that a user was found

        // Set up the User details to be returned by the ResultSet mock
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("username")).thenReturn("username");
        when(resultSetMock.getString("password")).thenReturn("password");
        when(resultSetMock.getString("first_name")).thenReturn("firstName");
        when(resultSetMock.getString("last_name")).thenReturn("lastName");
        when(resultSetMock.getString("role")).thenReturn("role");
        when(resultSetMock.getDate("birthday")).thenReturn(null);

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        User user = userRepository.login("username", "password");

        // We check the result to see if the user details match the mock
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals("role", user.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "username");
        verify(preparedStatementMock).setString(2, "password");
        verify(preparedStatementMock).executeQuery();


    }

    @Test
    public void loginIncorrectTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // indicate that no user was found

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test - and we check result - we expect a LoginSampleException
        assertThrows(LoginSampleException.class, () -> {
            userRepository.login("username", "password");
        });

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setString(1, "username");
        verify(preparedStatementMock).setString(2, "password");
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void getAllMembersTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Indicate that one user was found
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("username")).thenReturn("username");
        when(resultSetMock.getString("password")).thenReturn("password");
        when(resultSetMock.getString("first_name")).thenReturn("firstName");
        when(resultSetMock.getString("last_name")).thenReturn("lastName");
        when(resultSetMock.getString("role")).thenReturn("project_member");

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        List<User> users = userRepository.getAllMembers();

        // We check the result to see if 1 user is found
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals("project_member", user.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void getMembersOfProjectTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Indicate that one user was found
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("username")).thenReturn("username");
        when(resultSetMock.getString("password")).thenReturn("password");
        when(resultSetMock.getString("first_name")).thenReturn("firstName");
        when(resultSetMock.getString("last_name")).thenReturn("lastName");
        when(resultSetMock.getString("role")).thenReturn("project_member");

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        List<User> users = userRepository.getMembersOfProject(1);

        // We check the result, as in the previous test - should be 1 user
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals("project_member", user.getRole());

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
    }

    @Test
    public void isUserMemberOfProjectTest() throws SQLException {
        //We mock Connection, PreparedStatement and ResultSet classes to be used to retrieve data
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // And then define what should happen when they are called
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true); // Indicate that the user is a member of the project

        // We mock the DBManager
        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        // And then create the userRepository to use the mocked DBManager
        UserRepository userRepository = new UserRepository(dbManagerMock);

        // Call the method under test
        boolean isMember = userRepository.isUserMemberOfProject(1, 1);

        // We check the result, should return true if user is member
        assertTrue(isMember);

        // At last, we verify to see if the right methods were called on the mocked objects,
        // to make sure the test interacts correctly with the mocked database
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).executeQuery();
    }

}

