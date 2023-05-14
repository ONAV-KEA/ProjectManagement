package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;

import java.util.List;

public interface IRepository {
    List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id);
    void addCommentToSubtask(int subtaskId, String comment);

    Subtask createSubtask(Subtask form, int taskId);

    void deleteSubtask(int taskId);

    Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId);

    List<Subtask> getSubtasksByTaskId(int taskId);

    boolean editSubtask(Task form, int subtaskId, int taskId);


    void updateSubtaskStatus(int taskId, String taskStatus);


    List<String> getCommentsForSubtask(int subtaskId);

    void deleteCommentsForSubtask(int subtaskId);
}
