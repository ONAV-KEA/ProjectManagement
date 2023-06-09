package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IUserRepository;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final IUserRepository repository;

    public UserService(ApplicationContext context, @Value("${userrepository.impl}") String impl){
        this.repository = (IUserRepository) context.getBean(impl);
    }

    public List<User> getAllUsers(){
        return repository.getAllUsers();
    }

    public User getUserByID(int id){
        return repository.getUserByID(id);
    }
    public void deleteUserByUsername(String username) {
        repository.deleteUserByUsername(username);
    }

    public User login(String username, String password) throws LoginSampleException {
        return repository.login(username, password);
    }

    public void createUser(User form){
        repository.createUser(form);
    }

    public User editUser(User form, int userId){
        System.out.println("User edited");
        return repository.editUser(form, userId);
    }

    public void deleteUser(int id) {
        repository.deleteUser(id);
    }

    public List<User> getAllMembers(){
        return repository.getAllMembers();
    }

    public List<User> getMembersOfProject(int projectId){
        return repository.getMembersOfProject(projectId);
    }

    public boolean isUserMemberOfProject(int userId, int projectId){
        return repository.isUserMemberOfProject(userId, projectId);
    }

    public User getAssignedUserBySubtaskId(int subtaskId){
        return repository.getAssignedUserBySubtaskId(subtaskId);
    }

    public List<User> getAllTaskAssignees(int taskId){
        return repository.getAllTaskAssignees(taskId);
    }
}
