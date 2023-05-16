package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.model.Invitation;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.repository.utility.DBManager;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository("invitation_repo")
public class InvitationRepository implements IInvitationRepository{

    private final DBManager dbManager;

    public InvitationRepository(DBManager dbManager) {
        this.dbManager = dbManager;
    }
    @Override
    public List<InvitationDTO> getInvitationsByUserId(int id) {
        List<InvitationDTO> invitations = new ArrayList<>();

        try {
            Connection con = dbManager.getConnection();
            String SQL = "SELECT i.*, p.* FROM invitations i JOIN project p ON i.project_id = p.id WHERE i.recipient_id = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Invitation invitation = new Invitation();
                invitation.setId(rs.getInt("i.id"));
                invitation.setSenderId(rs.getInt("i.sender_id"));
                invitation.setRecipientId(rs.getInt("i.recipient_id"));
                invitation.setProjectId(rs.getInt("i.project_id"));
                invitation.setStatus(rs.getString("i.status"));

                Project project = new Project();
                project.setId(rs.getInt("p.id"));
                project.setName(rs.getString("p.name"));

                InvitationDTO invitationDTO = new InvitationDTO(invitation, project);
                invitations.add(invitationDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invitations;
    }
}
