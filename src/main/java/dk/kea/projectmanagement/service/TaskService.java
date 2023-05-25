package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.repository.ITaskRepository;
import dk.kea.projectmanagement.repository.IUserRepository;
import dk.kea.projectmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final ITaskRepository repository;

    public TaskService(ApplicationContext context, @Value("${taskrepository.impl}") String impl){
        this.repository = (ITaskRepository) context.getBean(impl);
    }


    public List<Task> getTasksByProjectId(int projectId){
        return repository.getTasksByProjectId(projectId);
    }

    public Task createTask(Task form, int projectId){
        return repository.createTask(form, projectId);
    }

    public void deleteTask(int taskId){
        repository.deleteTask(taskId);
    }

    public Task getTaskById(int taskId, int projectId){
        return repository.getTaskById(taskId, projectId);
    }

    public boolean editTask(Task form, int taskId, int projectId){
        return repository.editTask(form, taskId, projectId);
    }

    public void updateTaskStatus(int taskId, String status){
        repository.updateTaskStatus(taskId, status);
    }

    public void completeTask(int taskId){
        repository.completeTask(taskId);
    }

    public List<String> getCommentsForTask(int taskId){
        return repository.getCommentsForTask(taskId);
    }

    public void deleteCommentsForTask(int taskId){
        repository.deleteCommentsForTask(taskId);
    }

    public void addCommentToTask(int taskId, String comment){
        repository.addCommentToTask(taskId, comment);
    }

    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id){
        return repository.getTasksWithSubtasksByProjectId(id);
    }

    public void updateTaskCostFromSubtasks(int taskId){
        repository.updateTaskCostFromSubtasks(taskId);
    }

    public void addAllSubtaskAssigneesToMainTask(int taskId){
        repository.addAllSubtaskAssigneesToMainTask(taskId);
    }

    public void updateTaskCompletionPercentage(int taskId, double percentageCompletion){
        repository.updateTaskCompletionPercentage(taskId, percentageCompletion);
    }

}
