package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.model.*;
import dk.kea.projectmanagement.service.*;
import dk.kea.projectmanagement.utility.LoginSampleException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    private UserService userService;
    private ProjectService projectService;
    private TaskService taskService;
    private SubtaskService subtaskService;
    private InvitationService invitationService;

    public Controller(UserService userService, ProjectService projectService, TaskService taskService, SubtaskService subtaskService, InvitationService invitationService){
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
        this.invitationService = invitationService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }


    @GetMapping({"/",""})
    public String index(HttpSession session){
        if(isLoggedIn(session)){
            return "redirect:/dashboard";
        }
        return "index";
    }

    @PostMapping({"/",""})
    public String indexPost(HttpSession session, @ModelAttribute User form, Model model) {
        try {
            User user = userService.login(form.getUsername(), form.getPassword());

            session.setAttribute("user", user);
            session.setAttribute("projects", projectService.getProjectByUserId(user.getId()));
            if (user.getRole().equals("admin")){
                return "redirect:/admin";
            }else{
                return "redirect:/dashboard";
            }

        } catch (LoginSampleException e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/template")
    public String template(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "template";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        List<InvitationDTO> invitations = invitationService.getInvitationsByUserId(user.getId());
        model.addAttribute("invitationsDTO", invitations);
        model.addAttribute("user", user);
        model.addAttribute("projects", projectService.getProjectByUserId(user.getId()));

        return "dashboard";
    }

    @GetMapping("/createproject")
    public String project(HttpSession session, Model model) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("project", new Project());
        return "createproject";
    }

    @PostMapping ("/createproject")
    public String returnProject (@ModelAttribute Project form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Project project = projectService.createProject(form, user);

        return "redirect:/dashboard";
    }


    @GetMapping("/admin")
    public String admin(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("createUser", new User());


        //Redirects to dashboard if user is not admin
        if (!user.getRole().equals("admin")){
            return "redirect:/dashboard";
        }

        return "admin";
    }

@PostMapping("/admin")
public String createUser(@ModelAttribute User form, HttpSession session) {
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        userService.createUser(form);

        return "redirect:/admin";
    }

    @GetMapping("/edituser/{id}")
    public String editUser(@PathVariable int id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("editUser", userService.getUserByID(id));

        return "redirect:/admin";
    }

    @PostMapping("/edituser/{id}")
    public String editUser(@PathVariable int id, @ModelAttribute User form, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");

        User editedUser = userService.editUser(form, id);

        session.setAttribute("user", editedUser);
        System.out.println(editedUser.getId());

        return "redirect:/admin";
    }

    @GetMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        userService.deleteUser(id);

        return "redirect:/admin";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/organisations")
    public String organisations(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        return "organisations";
    }

    @GetMapping("/projects")
    public String projects(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", projectService.getProjectByUserId(user.getId()));

        return "projects";
    }

    @GetMapping("/project/{id}")
    public String project(Model model, HttpSession session, @PathVariable("id") int id){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        // Set models
        model.addAttribute("user", user);
        model.addAttribute("project", projectService.getProjectById(id));
        model.addAttribute("tasks", taskService.getTasksByProjectId(id));
        model.addAttribute("subtasks", subtaskService.getSubtasksByProjectId(id));
        model.addAttribute("tasksAndSubtasks", taskService.getTasksWithSubtasksByProjectId(id));

        // Set sessions
        session.setAttribute("projectId", id);
        session.setAttribute("project", projectService.getProjectById(id));
        session.setAttribute("tasks", taskService.getTasksByProjectId(id));
        session.setAttribute("subtasks", subtaskService.getSubtasksByProjectId(id));
        List<Task> tasks = (List<Task>) session.getAttribute("tasks");
        List<Subtask> subtasks = (List<Subtask>) session.getAttribute("subtasks");

        return "project";
    }

    @GetMapping("/project/{projectId}/createtask")
    public String createTask(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("task", new Task());
        model.addAttribute("projectId", projectId);
        User user = (User) session.getAttribute("user");
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "createtask";
    }

    @PostMapping("/project/{projectId}/createtask")
    public String returnTask(@PathVariable int projectId, @ModelAttribute Task form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Task task = taskService.createTask(form, projectId);

        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/addtaskcomment")
    public String addCommentToTask(@RequestParam("taskId") int taskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        taskService.addCommentToTask(taskId, comment);

        return "redirect:/project/" + projectId;

    }

    @GetMapping("/project/{projectId}/createsubtask/{taskId}")
    public String createSubtask(@PathVariable int projectId, @PathVariable int taskId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("subtask", new Subtask());
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        User user = (User) session.getAttribute("user");
        return "createsubtask";
    }

    @PostMapping("/project/{projectId}/createsubtask/{taskId}")
    public String returnSubtask(@PathVariable int projectId, @PathVariable int taskId, @ModelAttribute Subtask form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Subtask subtask = subtaskService.createSubtask(form, taskId, projectId);

        return "redirect:/project/" + projectId;
    }
    @PostMapping("/project/{projectId}/addsubtaskcomment")
    public String addCommentToSubtask(@RequestParam("subtaskId") int subtaskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        subtaskService.addCommentToSubtask(subtaskId, comment);

        return "redirect:/project/" + projectId;

    }

    @GetMapping("/project/{projectId}/delete/{type}/{id}")
    public String deleteTaskOrSubtask(
            @PathVariable("projectId") int projectId,
            @PathVariable("type") String type,
            @PathVariable("id") int id,
            HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        if ("task".equals(type)) {
            taskService.deleteTask(id);
            System.out.println("Task deleted " + id);
        } else if ("subtask".equals(type)) {
            subtaskService.deleteSubtask(id);
            System.out.println("Subtask deleted " + id);
        }

        return "redirect:/project/" + projectId;
    }

    @GetMapping("/project/{projectId}/edittask/{taskId}")
    public String editTask(@PathVariable int projectId, @PathVariable int taskId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        Task task = taskService.getTaskById(taskId, projectId);
        if (task == null) {
            return "redirect:/project/" + projectId; // Redirects to project page if task is not found
        }
        Task form = new Task(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCost(),task.getStatus());
        model.addAttribute("taskForm", form);
        model.addAttribute("taskId", taskId);
        model.addAttribute("projectId", projectId);
        return "edittask";
    }

    @PostMapping("/project/{projectId}/edittask/{taskId}")
    public String updateTask(@PathVariable int projectId, @PathVariable int taskId, @ModelAttribute Task form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        boolean isEdited = taskService.editTask(form, taskId, projectId);
        if (!isEdited) {
            // TODO: Handle error if task is not updated, e.g., show an error message or redirect to an error page
        }
        return "redirect:/project/" + projectId;
    }

    @GetMapping("/project/{projectId}/editsubtask/{taskId}/{subtaskId}")
    public String editSubtask(@PathVariable int projectId, @PathVariable int subtaskId, @PathVariable int taskId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        System.out.println("Subtask id: " + subtaskId
        + "\nTask id: " + taskId
                + "\nProject id: " + projectId);
        Subtask task = subtaskService.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);
        if (task == null) {
            return "redirect:/project/" + projectId; // Redirects to project page if task is not found
        }
        Subtask form = new Subtask(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCost(),task.getStatus());
        model.addAttribute("taskForm", form);
        model.addAttribute("taskId", taskId);
        model.addAttribute("subtaskId", subtaskId);
        model.addAttribute("projectId", projectId);
        return "editsubtask";
    }

    @PostMapping("/project/{projectId}/editsubtask/{taskId}/{subtaskId}")
    public String updateSubtask(@PathVariable int projectId, @PathVariable int subtaskId, @PathVariable int taskId, @ModelAttribute Subtask form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        boolean isEdited = subtaskService.editSubtask(form, subtaskId, taskId);
        if (!isEdited) {
            // Handle error if task is not updated, e.g., show an error message or redirect to an error page
        }
        return "redirect:/project/" + projectId;
    }


    @PostMapping("/project/{projectId}/update-task-status/{taskId}")
    public String updateTaskStatus(@PathVariable int projectId, @PathVariable int taskId, HttpSession session, @RequestParam("taskStatus") String taskStatus) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        taskService.updateTaskStatus(taskId, taskStatus);
        if (taskStatus.equals("completed")) {
            taskService.completeTask(taskId);
        }
        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/update-subtask-status/{subtaskId}")
    public String updateSubtaskStatus(@PathVariable int projectId, @PathVariable int subtaskId, HttpSession session, @RequestParam("subtaskStatus") String subtaskStatus) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }

        System.out.println("Subtask status: " + subtaskStatus);
        subtaskService.updateSubtaskStatus(subtaskId, subtaskStatus);
        if (subtaskStatus.equals("completed")) {
            subtaskService.completeSubtask(subtaskId);
        }

        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }

    @GetMapping("/project/{projectId}/projectsettings")
    public String projectSettings(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/"; // Redirects to project page if task is not found
        }
        Project form = new Project(project.getId(),project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());
        model.addAttribute("projectForm", form);
        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        return "projectsettings";
    }

    @PostMapping("/project/{projectId}/projectsettings")
    public String updateProject(@PathVariable int projectId, @ModelAttribute Project form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        projectService.editProject(form, projectId);

        return "redirect:/project/" + projectId;
    }

    @GetMapping("/project/{projectId}/deleteproject")
    public String deleteProject(@PathVariable int projectId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");

        //Retrieve all tasks from session
        List<Task> tasks = (List<Task>) session.getAttribute("tasks");
        for (Task task : tasks) {
            taskService.deleteTask(task.getId());
            taskService.deleteCommentsForTask(task.getId());
        }
        //Retrieve all subtasks from session
        List<Subtask> subtasks = (List<Subtask>) session.getAttribute("subtasks");
        for (Subtask subtask : subtasks) {
            subtaskService.deleteSubtask(subtask.getId());
            subtaskService.deleteCommentsForSubtask(subtask.getId());
        }
        projectService.deleteProject(projectId, user.getId());
        return "redirect:/projects";
    }

    @GetMapping("/project/{projectId}/invitemember")
    public String getInviteMember(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        User sender = (User) session.getAttribute("user");

        Project project = (Project) session.getAttribute("project");
        if (project == null) {
            return "redirect:/"; // Redirects to project page if task is not found
        }
        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        model.addAttribute("senderId", sender.getId());
        model.addAttribute("members", userService.getAllMembers());
        return "invitemember";
    }

    @PostMapping("/project/{projectId}/invitemember")
    public String inviteMember(@PathVariable int projectId, @RequestParam("senderId") int senderId, @RequestParam("recipientId") int recipientId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        User sender = (User) session.getAttribute("user");
        User recipient = userService.getUserByID(recipientId);
        Project project = (Project) session.getAttribute("project");
        if (project == null) {
            return "redirect:/"; // Redirects to project page if task is not found
        }
        projectService.inviteMember(senderId, recipientId, projectId);
        return "redirect:/project/" + projectId;
    }


}
