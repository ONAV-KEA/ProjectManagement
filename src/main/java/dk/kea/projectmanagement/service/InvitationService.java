package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.model.Invitation;
import dk.kea.projectmanagement.repository.IInvitationRepository;
import dk.kea.projectmanagement.repository.IProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {
    private final IInvitationRepository repository;

    public InvitationService(@Autowired IInvitationRepository repository){
        this.repository = repository;
    }

    public List<InvitationDTO> getInvitationsByUserId(int id) {
        return repository.getInvitationsByUserId(id);
    }

    public void acceptInvitation(int invitationId, int userId, int projectId) {
        repository.acceptInvitation(invitationId, userId, projectId);
    }

    public void declineInvitation(int invitationId) {
        repository.declineInvitation(invitationId);
    }
}
