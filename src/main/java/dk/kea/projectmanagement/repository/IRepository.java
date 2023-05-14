package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;

import java.util.List;

public interface IRepository {
    List<Task> getTasksByProjectId(int projectId);
    Task createTask(Task form, int projectId);
    List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id);
    void addCommentToTask(int taskId, String comment);
    void addCommentToSubtask(int subtaskId, String comment);

    Subtask createSubtask(Subtask form, int taskId);
    void deleteTask(int taskId);

    void deleteSubtask(int taskId);

    Task getTaskById(int taskId, int projectId);

    Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId);

    List<Subtask> getSubtasksByTaskId(int taskId);

    boolean editTask(Task form, int taskId, int projectId);

    boolean editSubtask(Task form, int subtaskId, int taskId);

    void updateTaskStatus(int taskId, String status);

    void updateSubtaskStatus(int taskId, String taskStatus);

    List<String> getCommentsForTask(int taskId);

    List<String> getCommentsForSubtask(int subtaskId);

    void completeTask(int taskId, int subtaskId);

    void deleteCommentsForTask(int taskId);
    void deleteCommentsForSubtask(int subtaskId);
}
