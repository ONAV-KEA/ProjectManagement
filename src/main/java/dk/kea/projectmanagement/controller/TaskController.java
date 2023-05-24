package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {
    private ProjectService projectService;
    private TaskService taskService;
    private SubtaskService subtaskService;

    public TaskController(ProjectService projectService, TaskService taskService, SubtaskService subtaskService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/project/{projectId}/createtask")
    public String createTask(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
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
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Task task = taskService.createTask(form, projectId);

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
        Task form = new Task(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCost(), task.getStatus());
        model.addAttribute("taskForm", form);
        model.addAttribute("taskId", taskId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));
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

    @PostMapping("/project/{projectId}/addtaskcomment")
    public String addCommentToTask(@RequestParam("taskId") int taskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        taskService.addCommentToTask(taskId, comment);

        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/update-task-status/{taskId}")
    public String updateTaskStatus(@PathVariable int projectId, @PathVariable int taskId, HttpSession session, @RequestParam("taskStatus") String taskStatus) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        taskService.updateTaskStatus(taskId, taskStatus);
        if (taskStatus.equals("completed")) {
            taskService.completeTask(taskId);
        }
        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }
}
