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

    public void deleteSubtask(int subtaskId){
        repository.deleteSubtask(subtaskId);
    }

    public void updateSubtaskStatus(int subtaskId, String status){
        repository.updateSubtaskStatus(subtaskId, status);
    }

    public Subtask createSubtask(Subtask form, int taskId){
        return repository.createSubtask(form, taskId);
    }


    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id){
        return repository.getTasksWithSubtasksByProjectId(id);
    }

    public void addCommentToSubtask(int subtaskId, String comment){
        repository.addCommentToSubtask(subtaskId, comment);
    }

    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId){
        return repository.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);
    }

    public List<Subtask> getSubtasksByTaskId(int taskId){
        return repository.getSubtasksByTaskId(taskId);
    }

    public boolean editSubtask(Task form, int subtaskId, int taskId){
        return repository.editSubtask(form, subtaskId, taskId);
    }

    public void deleteCommentsForSubtask(int subtaskId){
        repository.deleteCommentsForSubtask(subtaskId);
    }

}
