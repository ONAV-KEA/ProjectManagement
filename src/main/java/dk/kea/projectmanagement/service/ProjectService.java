package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IProjectRepository;
import dk.kea.projectmanagement.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private IProjectRepository repository;

    public ProjectService(ApplicationContext context, @Value("${projectrepository.impl}") String impl){
        repository = (IProjectRepository) context.getBean(impl);
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
}
