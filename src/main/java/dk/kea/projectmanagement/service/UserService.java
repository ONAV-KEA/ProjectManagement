package dk.kea.projectmanagement.service;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IUserRepository;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private IUserRepository repository;

    public UserService(ApplicationContext context, @Value("${userrepository.impl}") String impl){
        repository = (IUserRepository) context.getBean(impl);
    }

    public List<User> getAllUsers(){
        return repository.getAllUsers();
    }

    public User getUserByID(int id){
        return repository.getUserByID(id);
    }

    public User login(String username, String password) throws LoginSampleException {
        return repository.login(username, password);
    }

    public void createUser(User form){
        repository.createUser(form);
    }

    public void editUser(User form, int userId){
        repository.editUser(form, userId);
    }
}
