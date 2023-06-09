package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.model.Invitation;

import java.util.List;

public interface IInvitationRepository {
    List<InvitationDTO> getInvitationsByUserId(int id);

    void acceptInvitation(int invitationId, int userId, int projectId);

    void declineInvitation(int invitationId);
}
