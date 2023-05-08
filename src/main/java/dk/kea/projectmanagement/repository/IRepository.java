package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dk.kea.projectmanagement.dto.ProjectFormDTO;
import dk.kea.projectmanagement.dto.SubtaskFormDTO;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.dto.TaskFormDTO;

import java.util.List;

public interface IRepository {

    List<User> getAllUsers();
    User getUserByID(int id);
    User login(String username, String password) throws LoginSampleException;
    List<Project> getProjectByUserId(int id);
    Project createProject(ProjectFormDTO form, User user);
    List<Task> getTasksByProjectId(int projectId);
    Task createTask(TaskFormDTO form, int projectId);
    Project getProjectById(int id);
    List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id);
    void addCommentToTask(int taskId, String comment);
    void addCommentToSubtask(int subtaskId, String comment);

    Subtask createSubtask(SubtaskFormDTO form, int taskId);
    void deleteTask(int taskId);

    void deleteSubtask(int taskId);

    Task getTaskById(int taskId, int projectId);

    boolean editTask(TaskFormDTO form, int taskId, int projectId);

    void updateTaskStatus(int taskId, String status);

    void updateSubtaskStatus(int taskId, String taskStatus);

    void createUser(User form);

    void editUser(User form, int userId);
}
