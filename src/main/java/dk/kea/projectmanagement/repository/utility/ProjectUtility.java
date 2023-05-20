package dk.kea.projectmanagement.repository.utility;

import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.service.SubtaskService;
import dk.kea.projectmanagement.service.TaskService;
import dk.kea.projectmanagement.service.UserService;

import java.util.List;

public class ProjectUtility {

    private UserService userService;
    private SubtaskService subtaskService;
    private TaskService taskService;

    public ProjectUtility(SubtaskService subtaskService, TaskService taskService){
        this.subtaskService = subtaskService;
        this.taskService = taskService;
    }

    public ProjectUtility(UserService userService) {
        this.userService = userService;
    }

    public boolean isUserMemberOfProject(int userId, int projectId){
        return userService.isUserMemberOfProject(userId, projectId);
    }

    public User getAssignedUserBySubtaskId(int subtaskId){
        return userService.getAssignedUserBySubtaskId(subtaskId);
    }

    public List<User> getAllTaskAssignees(int taskId){
        return userService.getAllTaskAssignees(taskId);
    }

    public void updateTaskCompletionPercentage(int taskId){
        List<Subtask> subtasks = subtaskService.getSubtasksByTaskId(taskId);

        if (subtasks.isEmpty()){
            return;
        } else {
            double totalPercentage = 0;
            for (Subtask subtask : subtasks){
                totalPercentage += subtask.getPercentageCompletion();
            }
            double averagePercentage = totalPercentage / subtasks.size();
            taskService.updateTaskCompletionPercentage(taskId, averagePercentage);
        }
    }
}
