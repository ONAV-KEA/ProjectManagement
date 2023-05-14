package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Task;

import java.util.List;

public interface ITaskRepository {
    List<Task> getTasksByProjectId(int projectId);
    Task createTask(Task form, int projectId);

    void deleteTask(int taskId);

    Task getTaskById(int taskId, int projectId);

    boolean editTask(Task form, int taskId, int projectId);

    void updateTaskStatus(int taskId, String status);

    void completeTask(int taskId);

    List<String> getCommentsForTask(int taskId);

    void deleteCommentsForTask(int taskId);

    void addCommentToTask(int taskId, String comment);

    List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id);
}
