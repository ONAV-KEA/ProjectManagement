package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.service.DBService;
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
    private DBService service;

    public Controller(DBService service){
        this.service = service;
    }

    @GetMapping({"/",""})
    public String index(HttpServletRequest request){
        if(request.getSession().getAttribute("id") != null){
            return "redirect:/dashboard";
        }
        return "index";
    }

    @PostMapping({"/",""})
    public String indexPost(HttpSession session, @ModelAttribute User form, Model model) {
        try {
            User user = service.login(form.getUsername(), form.getPassword());

            session.setAttribute("user", user);
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

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/template")
    public String template(Model model){
        model.addAttribute("users", service.getAllUsers());
        return "template";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", service.getProjectByUserId(user.getId()));

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
        Project project = service.createProject(form, user);

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
        model.addAttribute("users", service.getAllUsers());
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
        service.createUser(form);

        return "redirect:/admin";
    }


@PostMapping("/editUser/{id}")
public String editUser(@PathVariable int id, @ModelAttribute User form, HttpSession session) {
    if (!isLoggedIn(session)) {
        return "redirect:/";
    }
    User user = (User) session.getAttribute("user");
    service.editUser(form, id);

    return "redirect:/admin";
}

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable int id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("editUser", service.getUserByID(id));

        return "redirect:/admin";
    }

    @GetMapping("/projects")
    public String projects(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", service.getProjectByUserId(user.getId()));

        return "projects";
    }

    @GetMapping("/project/{id}")
    public String project(Model model, HttpSession session, @PathVariable("id") int id){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("project", service.getProjectById(id));
        model.addAttribute("tasks", service.getTasksWithSubtasksByProjectId(id));
        session.setAttribute("projectId", id);
        session.setAttribute("tasks", service.getTasksByProjectId(id));
        List<Task> tasks = (List<Task>) session.getAttribute("tasks");
        List<Subtask> subtasks = null;
        for (Task task : tasks) {
            subtasks = service.getSubtasksByTaskId(task.getId());
        }
        session.setAttribute("subtasks", subtasks);

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
        Project project = service.getProjectById(projectId);
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
        Task task = service.createTask(form, projectId);

        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/addtaskcomment")
    public String addCommentToTask(@RequestParam("taskId") int taskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        service.addCommentToTask(taskId, comment);

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
        Subtask subtask = service.createSubtask(form, taskId);

        return "redirect:/project/" + projectId;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @PostMapping("/project/{projectId}/addsubtaskcomment")
    public String addCommentToSubtask(@RequestParam("subtaskId") int subtaskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        service.addCommentToSubtask(subtaskId, comment);

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
            service.deleteTask(id);
            System.out.println("Task deleted " + id);
        } else if ("subtask".equals(type)) {
            service.deleteSubtask(id);
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
        Task task = service.getTaskById(taskId, projectId);
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
        boolean isEdited = service.editTask(form, taskId, projectId);
        if (!isEdited) {
            // Handle error if task is not updated, e.g., show an error message or redirect to an error page
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
        Subtask task = service.getSubtaskByTaskIdAndSubtaskId(subtaskId, taskId);
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
    public String updateSubtask(@PathVariable int projectId, @PathVariable int subtaskId, @PathVariable int taskId, @ModelAttribute Task form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        boolean isEdited = service.editSubtask(form, subtaskId, taskId);
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
        service.updateTaskStatus(taskId, taskStatus);
        if (taskStatus.equals("completed")) {
            service.completeTask(taskId, 0);
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
        service.updateSubtaskStatus(subtaskId, subtaskStatus);
        if (subtaskStatus.equals("completed")) {
            service.completeTask(0, subtaskId);
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
        Project project = service.getProjectById(projectId);
        if (project == null) {
            return "redirect:/"; // Redirects to project page if task is not found
        }
        Project form = new Project(project.getId(),project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());
        model.addAttribute("projectForm", form);
        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        return "projectsettings";
    }

    @GetMapping("/project/{projectId}/deleteproject")
    public String deleteProject(@PathVariable int projectId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        //Retrieve all tasks from session
        List<Task> tasks = (List<Task>) session.getAttribute("tasks");
        for (Task task : tasks) {
            service.deleteTask(task.getId());
            service.deleteComments(task.getId(),0);
        }
        //Retrieve all subtasks from session
        List<Subtask> subtasks = (List<Subtask>) session.getAttribute("subtasks");
        for (Subtask subtask : subtasks) {
            service.deleteSubtask(subtask.getId());
            service.deleteComments(0,subtask.getId());
        }
        service.deleteProject(projectId);
        return "redirect:/dashboard";
    }


}
