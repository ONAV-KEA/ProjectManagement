package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import dto.ProjectFormDTO;
import dto.SubtaskFormDTO;
import dto.TaskAndSubtaskDTO;
import dto.TaskFormDTO;

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
}