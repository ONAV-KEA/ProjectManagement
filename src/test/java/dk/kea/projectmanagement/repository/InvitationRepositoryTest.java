package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationRepositoryTest {
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
    public void getInvitationsByUserIdTest_Success() throws SQLException, SQLException {
        // Mock objects
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // Define test data
        int userId = 1;
        int invitationId = 1;
        int senderId = 2;
        int recipientId = 1;
        int projectId = 3;
        String status = "pending";
        String projectName = "Project 1";

        // Set up mock behaviors
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false); // Returning true for the first row, then false for the second time because there are no more rows
        when(resultSetMock.getInt("i.id")).thenReturn(invitationId);
        when(resultSetMock.getInt("i.sender_id")).thenReturn(senderId);
        when(resultSetMock.getInt("i.recipient_id")).thenReturn(recipientId);
        when(resultSetMock.getInt("i.project_id")).thenReturn(projectId);
        when(resultSetMock.getString("i.status")).thenReturn(status);
        when(resultSetMock.getInt("p.id")).thenReturn(projectId);
        when(resultSetMock.getString("p.name")).thenReturn(projectName);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        // Call the method under test
        List<InvitationDTO> invitations = invitationRepository.getInvitationsByUserId(userId);

        // Assertions
        assertEquals(1, invitations.size());
        InvitationDTO invitationDTO = invitations.get(0);
        assertEquals(invitationId, invitationDTO.getInvitation().getId());
        assertEquals(senderId, invitationDTO.getInvitation().getSenderId());
        assertEquals(recipientId, invitationDTO.getInvitation().getRecipientId());
        assertEquals(projectId, invitationDTO.getInvitation().getProjectId());
        assertEquals(status, invitationDTO.getInvitation().getStatus());
        assertEquals(projectId, invitationDTO.getProject().getId());
        assertEquals(projectName, invitationDTO.getProject().getName());

        // Verify mock interactions
        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, userId);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock, times(2)).next();
    }

    @Test
    public void getInvitationsByUserIdTest_InvalidUserId() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false); // No rows returned

        List<InvitationDTO> invitations = invitationRepository.getInvitationsByUserId(-1);

        assertTrue(invitations.isEmpty());

        verify(connectionMock).prepareStatement(any(String.class));
        verify(preparedStatementMock).setInt(1, -1);
        verify(preparedStatementMock).executeQuery();
        verify(resultSetMock).next();
    }

    @Test
    public void acceptInvitationTest_Success() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock1 = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);

        when(connectionMock.prepareStatement(eq("UPDATE invitations SET status = 'accepted' WHERE id = ?")))
                .thenReturn(preparedStatementMock1);
        when(connectionMock.prepareStatement(eq("INSERT INTO project_user (project_id, user_id) VALUES (?, ?)")))
                .thenReturn(preparedStatementMock2);

        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        invitationRepository.acceptInvitation(1, 2, 3);

        verify(connectionMock).setAutoCommit(false);
        verify(preparedStatementMock1).setInt(1, 1);
        verify(preparedStatementMock1).executeUpdate();
        verify(preparedStatementMock2).setInt(1, 3);
        verify(preparedStatementMock2).setInt(2, 2);
        verify(preparedStatementMock2).executeUpdate();
        verify(connectionMock).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void acceptInvitationTest_InvalidIds() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock1 = mock(PreparedStatement.class);
        PreparedStatement preparedStatementMock2 = mock(PreparedStatement.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(eq("UPDATE invitations SET status = 'accepted' WHERE id = ?")))
                .thenReturn(preparedStatementMock1);
        when(connectionMock.prepareStatement(eq("INSERT INTO project_user (project_id, user_id) VALUES (?, ?)")))
                .thenReturn(preparedStatementMock2);

        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        doThrow(new SQLException("Invalid IDs provided."))
                .when(preparedStatementMock1).setInt(anyInt(), anyInt());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            invitationRepository.acceptInvitation(-1, 0, 0);
        });

        assertEquals("Could not accept invitation", exception.getMessage());

        verify(connectionMock, never()).commit();
        verify(connectionMock).setAutoCommit(true);
        verify(connectionMock).close();
    }

    @Test
    public void declineInvitationTest_Success() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(eq("UPDATE invitations SET status = 'declined' WHERE id = ?")))
                .thenReturn(preparedStatementMock);

        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        invitationRepository.declineInvitation(1);

        verify(preparedStatementMock).setInt(1, 1);
        verify(preparedStatementMock).executeUpdate();
        verifyNoMoreInteractions(preparedStatementMock);
        verify(connectionMock, never()).commit();
    }

    @Test
    public void declineInvitationTest_InvalidIds() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        DBManager dbManagerMock = mock(DBManager.class);
        when(dbManagerMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(eq("UPDATE invitations SET status = 'declined' WHERE id = ?")))
                .thenReturn(preparedStatementMock);

        InvitationRepository invitationRepository = new InvitationRepository(dbManagerMock);

        doThrow(new SQLException("Invalid IDs provided."))
                .when(preparedStatementMock).setInt(anyInt(), anyInt());

        invitationRepository.declineInvitation(-1);

        verify(preparedStatementMock).setInt(1, -1);
        verify(preparedStatementMock, never()).executeUpdate();
        verifyNoMoreInteractions(preparedStatementMock);
        verify(connectionMock, never()).commit();
    }

}