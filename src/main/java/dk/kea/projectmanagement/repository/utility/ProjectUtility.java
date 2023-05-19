package dk.kea.projectmanagement.repository.utility;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.service.UserService;

public class ProjectUtility {

    private UserService userService;

    public ProjectUtility(UserService userService){
        this.userService = userService;
    }

    public boolean isUserMemberOfProject(int userId, int projectId){
        return userService.isUserMemberOfProject(userId, projectId);
    }

    public User getAssignedUserBySubtaskId(int subtaskId){
        return userService.getAssignedUserBySubtaskId(subtaskId);
    }
}
