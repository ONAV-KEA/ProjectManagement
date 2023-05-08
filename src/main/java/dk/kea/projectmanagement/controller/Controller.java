package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.dto.ProjectFormDTO;
import dk.kea.projectmanagement.dto.SubtaskFormDTO;
import dk.kea.projectmanagement.dto.TaskFormDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IRepository;
import dk.kea.projectmanagement.utility.LoginSampleException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

@org.springframework.stereotype.Controller
public class Controller {
    IRepository repository;

    public Controller(ApplicationContext context, @Value("${repository.impl}") String impl){
        repository = (IRepository) context.getBean(impl);
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
            User user = repository.login(form.getUsername(), form.getPassword());

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
        model.addAttribute("users", repository.getAllUsers());
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
        model.addAttribute("projects", repository.getProjectByUserId(user.getId()));

        return "dashboard";
    }

    @GetMapping("/createProject")
    public String project(HttpSession session, Model model) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("project", new ProjectFormDTO());
        return "createProject";
    }

    @PostMapping ("/createProject")
    public String returnProject (@ModelAttribute ProjectFormDTO form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Project project = repository.createProject(form, user);

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
        model.addAttribute("users", repository.getAllUsers());

        //Redirects to dashboard if user is not admin
        if (!user.getRole().equals("admin")){
            return "redirect:/dashboard";
        }

        return "admin";
    }

    @GetMapping("/projects")
    public String projects(Model model, HttpSession session){
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", repository.getProjectByUserId(user.getId()));

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
        model.addAttribute("project", repository.getProjectById(id));
        model.addAttribute("tasks", repository.getTasksWithSubtasksByProjectId(id));

        return "project";
    }

    @GetMapping("/project/{projectId}/createtask")
    public String createTask(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("task", new TaskFormDTO());
        model.addAttribute("projectId", projectId);
        User user = (User) session.getAttribute("user");
        return "createtask";
    }

    @PostMapping("/project/{projectId}/createtask")
    public String returnTask(@PathVariable int projectId, @ModelAttribute TaskFormDTO form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Task task = repository.createTask(form, projectId);

        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/addtaskcomment")
    public String addCommentToTask(@RequestParam("taskId") int taskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        repository.addCommentToTask(taskId, comment);

        return "redirect:/project/" + projectId;

    }

    @GetMapping("/project/{projectId}/createsubtask/{taskId}")
    public String createSubtask(@PathVariable int projectId, @PathVariable int taskId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        model.addAttribute("subtask", new SubtaskFormDTO());
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        User user = (User) session.getAttribute("user");
        return "createsubtask";
    }

    @PostMapping("/project/{projectId}/createsubtask/{taskId}")
    public String returnSubtask(@PathVariable int projectId, @PathVariable int taskId, @ModelAttribute SubtaskFormDTO form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Subtask subtask = repository.createSubtask(form, taskId);

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
        repository.addCommentToSubtask(subtaskId, comment);

        return "redirect:/project/" + projectId;

    }

    @GetMapping ("/project/{projectId}/deletetask/{id}")
    public String deleteTask (@PathVariable("id") int id, @PathVariable("projectId") int projectId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        repository.deleteTask(id);
        System.out.println("Task deleted " + id);
        return "redirect:/project/" + projectId;
    }

    @GetMapping ("/project/{projectId}/deletesubtask/{id}")
    public String deleteSubtask (@PathVariable("id") int id, @PathVariable("projectId") int projectId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        repository.deleteSubtask(id);
        System.out.println("Subtask deleted " + id);
        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/update-task-status/{taskId}")
    public String updateTaskStatus(@PathVariable int projectId, @PathVariable int taskId, HttpSession session, @RequestParam("taskStatus") String taskStatus) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)){
            return "redirect:/";
        }
        repository.updateTaskStatus(taskId, taskStatus);
        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }

}
