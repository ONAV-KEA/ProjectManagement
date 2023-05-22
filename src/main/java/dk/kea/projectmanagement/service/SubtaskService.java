package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.repository.ISubtaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskService {

    private ISubtaskRepository repository;

    public SubtaskService(@Autowired ISubtaskRepository repository){
        this.repository = repository;
    }

    public void addCommentToSubtask(int subtaskId, String comment){
        repository.addCommentToSubtask(subtaskId, comment);
    }

    public void deleteSubtask(int subtaskId){
        repository.deleteSubtask(subtaskId);
    }

    public void updateSubtaskStatus(int subtaskId, String status){
        repository.updateSubtaskStatus(subtaskId, status);
    }

    public Subtask createSubtask(Subtask form, int taskId, int projectId){
        return repository.createSubtask(form, taskId, projectId);
    }

    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId){
        return repository.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);
    }

    public List<Subtask> getSubtasksByTaskId(int taskId){
        return repository.getSubtasksByTaskId(taskId);
    }

    public boolean editSubtask(Subtask form, int subtaskId, int taskId){
        return repository.editSubtask(form, subtaskId, taskId);
    }

    public void deleteCommentsForSubtask(int subtaskId){
        repository.deleteCommentsForSubtask(subtaskId);
    }

    public List<Subtask>  getSubtasksByProjectId(int projectId){
        return repository.getSubtasksByProjectId(projectId);
    }

    public List<String> getCommentsForSubtask(int subtaskId){
        return repository.getCommentsForSubtask(subtaskId);
    }

    public void completeSubtask(int subtaskId){
        repository.completeSubtask(subtaskId);
    }

    public void addUserToSubtask(int subtaskId, int userId){
        repository.addUserToSubtask(subtaskId, userId);
    }

    public void updatePercentage(int subtaskId, int percentage){
        repository.updatePercentage(subtaskId, percentage);
    }

    public List<Subtask> getSubtasksByUserId(int userId){
        return repository.getSubtasksByUserId(userId);
    }
}
