package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;

import java.util.List;

public interface IUserRepository {

    List<User> getAllUsers();
    User getUserByID(int id);
    User login(String username, String password) throws LoginSampleException;

    void createUser(User form);

    User editUser(User form, int userId);

    void deleteUser(int id);

    List<User> getAllMembers();

    List<User> getMembersOfProject(int projectId);

    boolean isUserMemberOfProject(int userId, int projectId);

    User getAssignedUserBySubtaskId(int subtaskId);

    List<User> getAllTaskAssignees(int taskId);
}
