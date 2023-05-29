package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.utility.LoginSampleException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Repository("stub_repo")
public class StubRepository implements IUserRepository, IProjectRepository, ITaskRepository, ISubtaskRepository, IInvitationRepository {

    // Test data
    User user1 = new User(1, "user1", "user1", "user1", "user1", LocalDate.now(), "project_member");
    User user2 = new User(2, "user2", "user2", "user2", "user2", LocalDate.now(), "project_member");
    User user3 = new User(3, "user3", "user3", "user3", "user3", LocalDate.now(), "project_manager");
    User user4 = new User(4, "user4", "user4", "user4", "user4", LocalDate.now(), "project_manager");
    User user5 = new User(5, "user5", "user5", "user5", "user5", LocalDate.now(), "admin");

    Project project1 = new Project(1, "project1", "project1", LocalDate.now(), LocalDate.now());
    Project project2 = new Project(2, "project2", "project2", LocalDate.now(), LocalDate.now());
    Project project3 = new Project(3, "project3", "project3", LocalDate.now(), LocalDate.now());
    Project project4 = new Project(4, "project4", "project4", LocalDate.now(), LocalDate.now());

    Task task1 = new Task(1, "task1", "task1", LocalDate.now(), LocalDate.now(), 300, 1);
    Task task2 = new Task(1, "task2", "task2", LocalDate.now(), LocalDate.now(), 565, 2);
    Task task3 = new Task(1, "task3", "task3", LocalDate.now(), LocalDate.now(), 43, 3);
    Task task4 = new Task(1, "task4", "task4", LocalDate.now(), LocalDate.now(), 312, 4);

    Subtask subtask1 = new Subtask(1, "Subtask 1", "Description of Subtask 1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10), 100.0, 1, 1, 0.0);
    Subtask subtask2 = new Subtask(2, "Subtask 2", "Description of Subtask 2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 15), 150.0, 2, 1, 50.0);
    Subtask subtask3 = new Subtask(3, "Subtask 3", "Description of Subtask 3", LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 31), 200.0, 3, 2, 25.0);
    Subtask subtask4 = new Subtask(4, "Subtask 4", "Description of Subtask 4", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 20), 120.0, 4, 2, 75.0);

    List<Project> projects = new ArrayList<>(Arrays.asList(project1, project2, project3, project4));
    List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3, user4, user5));
    List<Task> tasks = new ArrayList<>(Arrays.asList(task1, task2, task3, task4));
    List<Subtask> subtasks = new ArrayList<>(Arrays.asList(subtask1, subtask2, subtask3, subtask4));
    private Map<Integer, List<Integer>> userProjectMapping = new HashMap<>();
    private Map<Integer, List<Integer>> taskAssigneeMapping = new HashMap<>();
    List<String> comments = new ArrayList<>();

    public StubRepository() {
        populateUserProjectMapping();
    }

    // Helper method to populate the user-project mapping
    private void populateUserProjectMapping() {
        for (User user : users) {
            int userId = user.getId();
            List<Integer> projectIds = new ArrayList<>();

            for (Project project : projects) {
                // Simulating the relationship between users and projects
                if (isUserAssignedToProject(userId, project.getId())) {
                    projectIds.add(project.getId());
                }
            }

            userProjectMapping.put(userId, projectIds);
        }
    }

    // Helper method to check if a user is assigned to a project
    private boolean isUserAssignedToProject(int userId, int projectId) {
        // Simulating the check by iterating through the project-user relationship
        for (User user : users) {
            // Simulating the relationship between users and projects
            if (user.getId() == userId && isProjectAssignedToUser(projectId, userId)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to check if a project is assigned to a user
    private boolean isProjectAssignedToUser(int projectId, int userId) {
        // Simulating the check by iterating through the project-user relationship
        List<Integer> projectIds = userProjectMapping.get(userId);
        return projectIds != null && projectIds.contains(projectId);
    }

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
                break;
            }
        }
    }

    @Override
    public void deleteUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                users.remove(user);
                break;
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
        List<Integer> userIds = userProjectMapping.get(projectId);
        if (userIds != null) {
            for (User user : users) {
                if (userIds.contains(user.getId())) {
                    membersOfProject.add(user);
                }
            }
        }
        return membersOfProject;
    }

    @Override
    public boolean isUserMemberOfProject(int userId, int projectId) {
        List<Integer> userIds = userProjectMapping.get(projectId);
        return userIds != null && userIds.contains(userId);
    }

    @Override
    public User getAssignedUserBySubtaskId(int subtaskId) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId) {
                int assigneeId = subtask.getAssigneeId();
                for (User user : users) {
                    if (user.getId() == assigneeId) {
                        return user;
                    }
                }
                break;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllTaskAssignees(int taskId) {
        List<User> taskAssignees = new ArrayList<>();
        List<Integer> assigneeIds = taskAssigneeMapping.get(taskId);
        if (assigneeIds != null) {
            for (User user : users) {
                if (assigneeIds.contains(user.getId())) {
                    taskAssignees.add(user);
                }
            }
        }
        return taskAssignees;
    }

    @Override
    public List<Project> getProjectByUserId(int userId) {
        List<Project> userProjects = new ArrayList<>();
        for (Project project : projects) {
            if (isUserAssignedToProject(userId, project.getId())) {
                userProjects.add(project);
            }
        }
        return userProjects;
    }

    @Override
    public Project createProject(Project form, User user) {
        int id = projects.get(projects.size() - 1).getId() + 1;
        Project project = new Project(id, form.getName(), form.getDescription(), form.getStartDate(), form.getEndDate());
        projects.add(project);

        if (!userProjectMapping.containsKey(user.getId())) {
            userProjectMapping.put(user.getId(), new ArrayList<>());
        }
        List<Integer> projectIds = userProjectMapping.get(user.getId());
        projectIds.add(project.getId());

        return project;
    }

    @Override
    public Project getProjectById(int id) {
        for (Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    @Override
    public void deleteProject(int projectId, int userId) {
        for (Project project : projects) {
            if (project.getId() == projectId) {
                projects.remove(project);
                break;
            }
        }
        if (userProjectMapping.containsKey(userId)) {
            List<Integer> projectIds = userProjectMapping.get(userId);
            projectIds.removeIf(projectId1 -> projectId1 == projectId);
        }
    }

    @Override
    public void editProject(Project form, int projectId) {
        for (Project project : projects) {
            if (project.getId() == projectId) {
                // Update the project's properties if the form fields are not null, otherwise keep the original values
                project.setName(form.getName() != null ? form.getName() : project.getName());
                project.setDescription(form.getDescription() != null ? form.getDescription() : project.getDescription());
                project.setStartDate(form.getStartDate() != null ? form.getStartDate() : project.getStartDate());
                project.setEndDate(form.getEndDate() != null ? form.getEndDate() : project.getEndDate());
                break;
            }
        }
    }


    @Override
    public List<Map<String, Object>> createGanttData(List<TaskAndSubtaskDTO> tasksAndSubtasks) {
        List<Map<String, Object>> ganttData = new ArrayList<>();

        for (TaskAndSubtaskDTO task : tasksAndSubtasks) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", String.valueOf(task.getId()));  // Convert to string
            taskData.put("name", task.getName());
            taskData.put("start", getEpochMillis(task.getStartDate()));
            taskData.put("end", getEpochMillis(task.getEndDate()));
            taskData.put("completed", task.getPercentageCompletion() / 100.0);

            ganttData.add(taskData);

            for (Subtask subtask : task.getSubtasks()) {
                Map<String, Object> subtaskData = new HashMap<>();
                subtaskData.put("id", String.valueOf(subtask.getId()));  // Convert to string
                subtaskData.put("parent", String.valueOf(task.getId()));  // Convert to string
                subtaskData.put("name", subtask.getTitle());
                subtaskData.put("start", getEpochMillis(subtask.getStartDate()));
                subtaskData.put("end", getEpochMillis(subtask.getEndDate()));
                subtaskData.put("completed", subtask.getPercentageCompletion() / 100.0);

                ganttData.add(subtaskData);
            }
        }

        return ganttData;
    }

    private Long getEpochMillis(LocalDate date) {
        if (date != null) {
            return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return null;
    }


    @Override
    public void inviteMember(int senderId, int recipientId, int projectId) {
        List<Integer> projectMembers = userProjectMapping.get(projectId);
        if (projectMembers != null) {
            if (!projectMembers.contains(recipientId)) {
                projectMembers.add(recipientId);
            }
        } else {
            projectMembers = new ArrayList<>();
            projectMembers.add(recipientId);
            userProjectMapping.put(projectId, projectMembers);
        }
    }

    @Override
    public void deleteProjectMember(int projectId, int userId) {
        List<Integer> projectMembers = userProjectMapping.get(projectId);
        if (projectMembers != null) {
            projectMembers.remove(Integer.valueOf(userId));
        }
    }


    @Override
    public int getTotalProjectCost(int projectId) {
        int totalCost = 0;
        List<Task> projectTasks = getTasksByProjectId(projectId);

        if (projectTasks != null) {
            for (Task task : projectTasks) {
                totalCost += task.getCost();
            }
        }

        return totalCost;
    }



    @Override
    public void addCommentToSubtask(int subtaskId, String comment) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId) {
                subtask.setComments(Collections.singletonList(comment));
                break;
            }
        }
    }

    @Override
    public Subtask createSubtask(Subtask form, int taskId, int projectId) {
        // get id of last subtask in list and increment by 1
        int id = subtasks.get(subtasks.size() - 1).getId() + 1;
        Subtask subtask = new Subtask(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getCost(), taskId, projectId, 0);
        subtasks.add(subtask);
        return subtask;
    }

    @Override
    public void deleteSubtask(int taskId) {
        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == taskId) {
                subtasks.remove(subtask);
                break;
            }
        }
    }

    @Override
    public Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId && subtask.getTaskId() == taskId) {
                return subtask;
            }
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtasksByTaskId(int taskId) {
        List<Subtask> matchingSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == taskId) {
                matchingSubtasks.add(subtask);
            }
        }
        return matchingSubtasks;
    }

    @Override
    public boolean editSubtask(Subtask form, int subtaskId, int taskId) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId && subtask.getTaskId() == taskId) {
                subtask.setTitle(form.getTitle());
                subtask.setDescription(form.getDescription());
                subtask.setStartDate(form.getStartDate());
                subtask.setEndDate(form.getEndDate());
                subtask.setCost(form.getCost());
                subtask.setPercentageCompletion(form.getPercentageCompletion());
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateSubtaskStatus(int taskId, String taskStatus) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == taskId) {
                subtask.setStatus(taskStatus);
                break;
            }
        }
    }

    @Override
    public List<String> getCommentsForSubtask(int subtaskId) {
        return null;
    }

    @Override
    public void deleteCommentsForSubtask(int subtaskId) {

    }

    @Override
    public List<Subtask> getSubtasksByProjectId(int projectId) {
        List<Subtask> projectSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getProjectId() == projectId) {
                projectSubtasks.add(subtask);
            }
        }
        return projectSubtasks;
    }

    @Override
    public void completeSubtask(int subtaskId) {
        // Update the completion status of the subtask to 100% and set the end date
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId) {
                subtask.setPercentageCompletion(100);
                subtask.setEndDate(LocalDate.now());
                subtask.setStatus("completed");
                break;
            }
        }
    }


    @Override
    public void addUserToSubtask(int subtaskId, int userId) {

    }

    @Override
    public void updatePercentage(int subtaskId, int percentage) {
        // Update the completion percentage of the subtask
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId) {
                subtask.setPercentageCompletion(percentage);
                break;
            }
        }
    }


    @Override
    public List<Subtask> getSubtasksByUserId(int userId) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            if (subtask.getAssigneeId() == userId) {
                subtasks.add(subtask);
            }
        }

        return subtasks;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtaskId) {
                return subtask;
            }
        }

        return null;
    }


    @Override
    public List<Task> getTasksByProjectId(int projectId) {
        return null;
    }

    @Override
    public Task createTask(Task form, int projectId) {
        int id = tasks.get(tasks.size() - 1).getId() + 1;
        Task task = new Task(id, form.getTitle(), form.getDescription(), form.getStartDate(), form.getEndDate(), form.getCost(), projectId);
        tasks.add(task);
        return task;
    }


    @Override
    public void deleteTask(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                tasks.remove(task);
                break;
            }
        }
    }

    @Override
    public Task getTaskById(int taskId, int projectId) {
        for (Task task : tasks) {
            if (task.getId() == taskId && task.getProjectId() == projectId) {
                return task;
            }
        }
        return null;
    }

    @Override
    public boolean editTask(Task form, int taskId, int projectId) {
        for (Task task : tasks) {
            if (task.getId() == taskId && task.getProjectId() == projectId) {
                // Update the task's properties if the form fields are not null, otherwise keep the original values
                task.setTitle(form.getTitle() != null ? form.getTitle() : task.getTitle());
                task.setDescription(form.getDescription() != null ? form.getDescription() : task.getDescription());
                task.setStartDate(form.getStartDate() != null ? form.getStartDate() : task.getStartDate());
                task.setCost(form.getCost());
                task.setEndDate(form.getEndDate() != null ? form.getEndDate() : task.getEndDate());
                task.setStatus(form.getStatus() != null ? form.getStatus() : task.getStatus());
                return true;
            }
        }
        return false;
    }



    @Override
    public void updateTaskStatus(int taskId, String status) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                task.setStatus(status);
                break;
            }
        }
    }

    @Override
    public void completeTask(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                task.setPercentageCompletion(100);
                task.setEndDate(LocalDate.now());
                task.setStatus("completed");
                break;
            }
        }
    }

    @Override
    public List<String> getCommentsForTask(int taskId) {
           return null;
    }

    @Override
    public void deleteCommentsForTask(int taskId) {

    }

    @Override
    public void addCommentToTask(int taskId, String comment) {

    }

    @Override
    public List<TaskAndSubtaskDTO> getTasksWithSubtasksByProjectId(int id) {
        List<TaskAndSubtaskDTO> tasksWithSubtasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getProjectId() == id) {
                List<Subtask> subtasksForTask = new ArrayList<>();

                for (Subtask subtask : subtasks) {
                    if (subtask.getTaskId() == task.getId()) {
                        subtasksForTask.add(subtask);
                    }
                }

                TaskAndSubtaskDTO taskAndSubtaskDTO = new TaskAndSubtaskDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStartDate(),
                        task.getEndDate(),
                        task.getAssigneeId(),
                        task.getCost(),
                        task.getStatus(),
                        task.getComments(),
                        task.getProjectId(),
                        subtasksForTask,
                        task.getPercentageCompletion()
                );

                tasksWithSubtasks.add(taskAndSubtaskDTO);
            }
        }

        return tasksWithSubtasks;
    }


    @Override
    public void updateTaskCostFromSubtasks(int taskId) {
        List<Subtask> subtasks = getSubtasksByTaskId(taskId); // Assuming you have implemented the getSubtasksByTaskId method

        double totalCost = 0.0;
        for (Subtask subtask : subtasks) {
            totalCost += subtask.getCost();
        }

        // Update the task's cost
        Task task = getTaskById(taskId); // Assuming you have implemented the getTaskById method
        task.setCost(totalCost);
    }


    @Override
    public void addMemberToTask(int taskId, int memberId) {
        List<Integer> assigneeIds = taskAssigneeMapping.getOrDefault(taskId, new ArrayList<>());
        assigneeIds.add(memberId);
        taskAssigneeMapping.put(taskId, assigneeIds);
    }


    @Override
    public void removeMemberFromTask(int taskId, int memberId) {
        List<Integer> assignedMembers = taskAssigneeMapping.get(taskId);

        if (assignedMembers != null) {
            assignedMembers.remove(Integer.valueOf(memberId));
        }
    }


    @Override
    public void addAllSubtaskAssigneesToMainTask(int taskId) {

    }

    @Override
    public void updateTaskCompletionPercentage(int taskId, double percentageCompletion) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setPercentageCompletion(percentageCompletion);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }

        return null;
    }


    @Override
    public List<InvitationDTO> getInvitationsByUserId(int id) {
        return null;
    }

    @Override
    public void acceptInvitation(int invitationId, int userId, int projectId) {

    }

    @Override
    public void declineInvitation(int invitationId) {

    }
}
