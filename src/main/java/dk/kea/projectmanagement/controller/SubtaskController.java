package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.ProjectUtility;
import dk.kea.projectmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubtaskController {
    private ProjectService projectService;
    private TaskService taskService;
    private SubtaskService subtaskService;

    public SubtaskController(ProjectService projectService, TaskService taskService, SubtaskService subtaskService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/project/{projectId}/createsubtask/{taskId}")
    public String createSubtask(@PathVariable int projectId, @PathVariable int taskId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        model.addAttribute("subtask", new Subtask());
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("project", projectService.getProjectById(projectId));
        User user = (User) session.getAttribute("user");
        return "createsubtask";
    }

    @PostMapping("/project/{projectId}/createsubtask/{taskId}")
    public String returnSubtask(@PathVariable int projectId, @PathVariable int taskId, @ModelAttribute Subtask form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Subtask subtask = subtaskService.createSubtask(form, taskId, projectId);

        taskService.updateTaskCostFromSubtasks(taskId);
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
        Subtask form = new Subtask(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCost(), task.getStatus());
        model.addAttribute("taskForm", form);
        model.addAttribute("taskId", taskId);
        model.addAttribute("subtaskId", subtaskId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));
        return "editsubtask";
    }

    @PostMapping("/project/{projectId}/editsubtask/{taskId}/{subtaskId}")
    public String updateSubtask(@PathVariable int projectId, @PathVariable int subtaskId, @PathVariable int taskId, @ModelAttribute Subtask form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        boolean isEdited = subtaskService.editSubtask(form, subtaskId, taskId);
        // Check if subtask cost is updated
        if (isEdited) {
            taskService.updateTaskCostFromSubtasks(taskId);
        }
        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/addsubtaskcomment")
    public String addCommentToSubtask(@RequestParam("subtaskId") int subtaskId, @RequestParam("comment") String comment, HttpSession session, @PathVariable int projectId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        subtaskService.addCommentToSubtask(subtaskId, comment);

        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/update-subtask-status/{subtaskId}")
    public String updateSubtaskStatus(@PathVariable int projectId, @PathVariable int subtaskId, HttpSession session, @RequestParam("subtaskStatus") String subtaskStatus, @RequestParam("taskId") int taskId) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        System.out.println("Subtask status: " + subtaskStatus);
        subtaskService.updateSubtaskStatus(subtaskId, subtaskStatus);
        // Check if subtaskStatus is "completed"
        if (subtaskStatus.equals("completed")) {
            subtaskService.completeSubtask(subtaskId);

            // Check if all subtasks are completed for the task
            List<Subtask> subtasks = subtaskService.getSubtasksByTaskId(subtaskId);
            boolean allSubtasksCompleted = true;
            for (Subtask subtask : subtasks) {
                if (!subtask.getStatus().equals("completed")) {
                    allSubtasksCompleted = false;
                    break;
                }
            }

            // If all subtasks are completed, update the task status to "completed"
            if (allSubtasksCompleted) {
                taskService.completeTask(taskId);
            }
        }


        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/addtosubtask")
    public String addToSubtask(@PathVariable int projectId, @RequestParam("subtaskId") int subtaskId, @RequestParam("userId") int userId, @RequestParam("taskId") int taskId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        subtaskService.addUserToSubtask(subtaskId, userId);
        taskService.addAllSubtaskAssigneesToMainTask(taskId);
        return "redirect:/project/" + projectId;
    }

    @PostMapping("/project/{projectId}/update-subtask-percentage/{subtaskId}")
    public String updateSubtaskPercentage(@PathVariable int projectId, @PathVariable int subtaskId, @RequestParam("subtaskPercentage") int subtaskPercentage, @RequestParam("taskId") int taskId, HttpSession session) {
        ProjectUtility projectUtility = new ProjectUtility(subtaskService, taskService);
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        System.out.println("Subtask percentage: " + subtaskPercentage);
        subtaskService.updatePercentage(subtaskId, subtaskPercentage);
        projectUtility.updateTaskCompletionPercentage(taskId);

        // if subtask percentage is 100, complete subtask
        if (subtaskPercentage == 100) {
            subtaskService.completeSubtask(subtaskId);
        }

        User user = (User) session.getAttribute("user");
        return "redirect:/project/" + projectId;
    }
}
