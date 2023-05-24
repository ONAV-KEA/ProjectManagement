package dk.kea.projectmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.kea.projectmanagement.dto.TaskAndSubtaskDTO;
import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.Subtask;
import dk.kea.projectmanagement.model.Task;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.ProjectUtility;
import dk.kea.projectmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    private UserService userService;
    private ProjectService projectService;
    private TaskService taskService;
    private SubtaskService subtaskService;

    public ProjectController(UserService userService, ProjectService projectService, TaskService taskService, SubtaskService subtaskService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/createproject")
    public String project(HttpSession session, Model model) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        model.addAttribute("project", new Project());
        return "createproject";
    }

    @PostMapping("/createproject")
    public String returnProject(@ModelAttribute Project form, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        Project project = projectService.createProject(form, user);

        return "redirect:/dashboard";
    }

    @GetMapping("/projects")
    public String projects(Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", projectService.getProjectByUserId(user.getId()));

        return "projects";
    }

    @GetMapping("/project/{id}")
    public String project(Model model, HttpSession session, @PathVariable("id") int id) throws JsonProcessingException {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        // Set models
        model.addAttribute("user", user);
        model.addAttribute("project", projectService.getProjectById(id));
        model.addAttribute("tasks", taskService.getTasksByProjectId(id));
        model.addAttribute("subtasks", subtaskService.getSubtasksByProjectId(id));
        model.addAttribute("tasksAndSubtasks", taskService.getTasksWithSubtasksByProjectId(id));
        model.addAttribute("members", userService.getMembersOfProject(id));
        model.addAttribute("projectUtility", new ProjectUtility(userService));
        model.addAttribute("projectCost", projectService.getTotalProjectCost(id));

        // Set sessions
        session.setAttribute("projectId", id);
        session.setAttribute("project", projectService.getProjectById(id));
        session.setAttribute("tasks", taskService.getTasksByProjectId(id));
        session.setAttribute("subtasks", subtaskService.getSubtasksByProjectId(id));
        List<Task> tasks = (List<Task>) session.getAttribute("tasks");
        List<Subtask> subtasks = (List<Subtask>) session.getAttribute("subtasks");

        List<TaskAndSubtaskDTO> tasksAndSubtasks = taskService.getTasksWithSubtasksByProjectId(id);
        List<Map<String, Object>> ganttData = projectService.createGanttData(tasksAndSubtasks);
        ObjectMapper mapper = new ObjectMapper();
        String ganttDataJson = mapper.writeValueAsString(ganttData);
        model.addAttribute("ganttData", ganttDataJson);
        System.out.println(ganttDataJson);

        return "project";
    }

    @GetMapping("/project/{projectId}/projectsettings")
    public String projectSettings(@PathVariable int projectId, Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        //Redirects to project-page if user is not manager or admin
        User user = (User) session.getAttribute("user");
        if (user.getRole().equals("project_member")) {
            return "redirect:/project/" + projectId;
        }

        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/"; // Redirects to project page if task is not found
        }
        Project form = new Project(project.getId(), project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());
        model.addAttribute("projectForm", form);
        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectMembers", userService.getMembersOfProject(projectId));
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

    @GetMapping("/project/{projectId}/deleteprojectmember/{userId}")
    public String deleteProjectMember(@PathVariable int projectId, @PathVariable int userId, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        projectService.deleteProjectMember(projectId, userId);
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
}
