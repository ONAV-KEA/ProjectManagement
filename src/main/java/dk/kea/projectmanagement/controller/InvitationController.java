package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.utility.ProjectUtility;
import dk.kea.projectmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvitationController {
    private UserService userService;
    private ProjectService projectService;
    private InvitationService invitationService;

    public InvitationController(UserService userService, ProjectService projectService, InvitationService invitationService) {
        this.userService = userService;
        this.projectService = projectService;

        this.invitationService = invitationService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
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
        model.addAttribute("projectUtility", new ProjectUtility(userService));
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

    @PostMapping("invitation/accept")
    public String acceptInvitation(@RequestParam("invitationId") int invitationId, @RequestParam("userId") int userId, @RequestParam("projectId") int projectId) {
        invitationService.acceptInvitation(invitationId, userId, projectId);

        return "redirect:/dashboard";
    }

    @PostMapping("invitation/decline")
    public String declineInvitation(@RequestParam("invitationId") int invitationId) {
        invitationService.declineInvitation(invitationId);

        return "redirect:/dashboard";
    }
}
