package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IProjectRepository;
import dk.kea.projectmanagement.repository.ITaskRepository;
import dk.kea.projectmanagement.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final IProjectRepository repository;

    public ProjectService(ApplicationContext context, @Value("${projectrepository.impl}") String impl){
        this.repository = (IProjectRepository) context.getBean(impl);
    }

    public List<Project> getProjectByUserId(int id){
        return repository.getProjectByUserId(id);
    }

    public Project createProject(Project form, User user){
        return repository.createProject(form, user);
    }

    public Project getProjectById(int id){
        return repository.getProjectById(id);
    }

    public void deleteProject(int projectId, int userId){
        repository.deleteProject(projectId, userId);
    }

    public void editProject(Project form, int projectId){
        repository.editProject(form, projectId);
    }
    public List<Map<String, Object>> createGanttData(List<TaskAndSubtaskDTO> tasksAndSubtasks) {
        return repository.createGanttData(tasksAndSubtasks);
    }

    public void inviteMember(int senderId, int recipientId, int projectId){
        repository.inviteMember(senderId, recipientId, projectId);
    }

    public void deleteProjectMember(int projectId, int userId) {
        repository.deleteProjectMember(projectId, userId);
    }

    public int getTotalProjectCost(int projectId) {
        return repository.getTotalProjectCost(projectId);
    }
}
