package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;

import java.util.List;

public interface IProjectRepository {

    List<Project> getProjectByUserId(int id);
    Project createProject(Project form, User user);

    Project getProjectById(int id);

    void deleteProject(int projectId, int userId);

    void editProject(Project form, int projectId);

    List<List<Object>> createGanttData(List<TaskAndSubtaskDTO> tasks);

    void inviteMember(int senderId, int recipientId, int projectId);
}
