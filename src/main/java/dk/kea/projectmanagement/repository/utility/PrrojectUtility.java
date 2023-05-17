package dk.kea.projectmanagement.repository.utility;

import dk.kea.projectmanagement.service.UserService;

public class PrrojectUtility {

    private UserService userService;

    public PrrojectUtility(UserService userService){
        this.userService = userService;
    }

    public boolean isUserMemberOfProject(int userId, int projectId){
        return userService.isUserMemberOfProject(userId, projectId);
    }
}
