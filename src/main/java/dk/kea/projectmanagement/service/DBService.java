package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IRepository;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBService {

    private IRepository repository;

    public DBService(ApplicationContext context, @Value("${repository.impl}") String impl){
        repository = (IRepository) context.getBean(impl);
    }

    public List<User> getAllUsers(){
        return repository.getAllUsers();
    }

    public User getUserByID(int id){
        return repository.getUserByID(id);
    }

    public User login(String username, String password) throws LoginSampleException {
        return repository.login(username, password);
    }

    public List<Project> getProjectByUserId(int id){
        return repository.getProjectByUserId(id);
    }

    public Project createProject(Project form, User user){
        return repository.createProject(form, user);
    }

    public void deleteTask(int taskId){
        repository.deleteTask(taskId);
    }

    public void deleteSubtask(int subtaskId){
        repository.deleteSubtask(subtaskId);
    }

    public void updateTaskStatus(int taskId, String status){
        repository.updateTaskStatus(taskId, status);
    }

    public void updateSubtaskStatus(int subtaskId, String status){
        repository.updateSubtaskStatus(subtaskId, status);
    }

    public List<Task> getTasksByProjectId(int projectId){
        return repository.getTasksByProjectId(projectId);
    }

    public Task createTask(Task form, int projectId){
        return repository.createTask(form, projectId);
    }

    public Subtask createSubtask(Subtask form, int taskId){
        return repository.createSubtask(form, taskId);
    }

    public Project getProjectById(int id){
        return repository.getProjectById(id);
    }

    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id){
        return repository.getTasksWithSubtasksByProjectId(id);
    }

    public void addCommentToTask(int taskId, String comment){
        repository.addCommentToTask(taskId, comment);
    }

    public void addCommentToSubtask(int subtaskId, String comment){
        repository.addCommentToSubtask(subtaskId, comment);
    }

    public Task getTaskById(int taskId, int projectId){
        return repository.getTaskById(taskId, projectId);
    }

    public boolean editTask(Task form, int taskId, int projectId){
        return repository.editTask(form, taskId, projectId);
    }

    public void createUser(User form){
        repository.createUser(form);
    }

    public void editUser(User form, int userId){
        repository.editUser(form, userId);
    }

    public List<List<Object>> createGanttData(List<TaskAndSubtaskDTO> tasks) {
        return repository.createGanttData(tasks);
    }
}
