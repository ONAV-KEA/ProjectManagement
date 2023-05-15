package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IProjectRepository;
import dk.kea.projectmanagement.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final IProjectRepository repository;

    public ProjectService(@Autowired IProjectRepository repository){
        this.repository = repository;
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
}
