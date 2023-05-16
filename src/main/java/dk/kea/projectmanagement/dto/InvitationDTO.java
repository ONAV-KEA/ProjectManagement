package dk.kea.projectmanagement.dto;

import dk.kea.projectmanagement.model.Invitation;
import dk.kea.projectmanagement.model.Project;

public class InvitationDTO {
    private Invitation invitation;
    private Project project;

    public InvitationDTO(Invitation invitation, Project project) {
        this.invitation = invitation;
        this.project = project;
    }

    public InvitationDTO() {
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

