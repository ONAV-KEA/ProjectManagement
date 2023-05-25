package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository("stub_user_repo")
public class StubUserRepository implements IUserRepository{

    // Test data
    User user1 = new User(1, "user1", "user1", "user1", "user1", LocalDate.now(), "project_member");
    User user2 = new User(2, "user2", "user2", "user2", "user2", LocalDate.now(), "project_member");
    User user3 = new User(3, "user3", "user3", "user3", "user3", LocalDate.now(), "project_manager");
    User user4 = new User(4, "user4", "user4", "user4", "user4", LocalDate.now(), "project_manager");
    User user5 = new User(5, "user5", "user5", "user5", "user5", LocalDate.now(), "admin");

    List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3, user4, user5));

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserByID(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User login(String username, String password) throws LoginSampleException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new LoginSampleException("Incorrect username or password");
    }

    @Override
    public void createUser(User form) {
        // Get the last user in the list and add 1 to the id
        int id = users.get(users.size() - 1).getId() + 1;
        User user = new User(id, form.getUsername(), form.getPassword(), form.getFirstName(), form.getLastName(), form.getBirthday(), form.getRole());
        users.add(user);
    }

    @Override
    public User editUser(User form, int userId) {
        User userToUpdate = null;
        for (User user : users) {
            if (user.getId() == userId) {
                userToUpdate = user;
                break;
            }
        }
        if (userToUpdate != null) {
            // Update the user's properties if the form fields are not null, otherwise keep the original values
            userToUpdate.setUsername(form.getUsername() != null ? form.getUsername() : userToUpdate.getUsername());
            userToUpdate.setPassword(form.getPassword() != null ? form.getPassword() : userToUpdate.getPassword());
            userToUpdate.setFirstName(form.getFirstName() != null ? form.getFirstName() : userToUpdate.getFirstName());
            userToUpdate.setLastName(form.getLastName() != null ? form.getLastName() : userToUpdate.getLastName());
            userToUpdate.setBirthday(form.getBirthday() != null ? form.getBirthday() : userToUpdate.getBirthday());
            userToUpdate.setRole(form.getRole() != null ? form.getRole() : userToUpdate.getRole());
            return userToUpdate;
        } else {
            throw new RuntimeException("User not found");
        }
    }


    @Override
    public void deleteUser(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                users.remove(user);
            }
        }
    }

    @Override
    public List<User> getAllMembers() {
        List<User> members = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("project_member")) {
                members.add(user);
            }
        }
        return members;
    }

    @Override
    public List<User> getMembersOfProject(int projectId) {
        List<User> membersOfProject = new ArrayList<>();
        return null;
    }

    @Override
    public boolean isUserMemberOfProject(int userId, int projectId) {
        return false;
    }

    @Override
    public User getAssignedUserBySubtaskId(int subtaskId) {
        return null;
    }

    @Override
    public List<User> getAllTaskAssignees(int taskId) {
        return null;
    }
}
