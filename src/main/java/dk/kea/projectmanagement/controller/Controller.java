package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.dto.InvitationDTO;
import dk.kea.projectmanagement.model.*;
import dk.kea.projectmanagement.service.*;
import dk.kea.projectmanagement.utility.LoginSampleException;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    private UserService userService;
    private ProjectService projectService;
    private TaskService taskService;
    private SubtaskService subtaskService;
    private InvitationService invitationService;

    public Controller(UserService userService, ProjectService projectService, TaskService taskService, SubtaskService subtaskService, InvitationService invitationService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
        this.invitationService = invitationService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }


    @GetMapping({"/", ""})
    public String index(HttpSession session) {
        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @PostMapping({"/", ""})
    public String indexPost(HttpSession session, @ModelAttribute User form, Model model) {
        try {
            User user = userService.login(form.getUsername(), form.getPassword());

            session.setAttribute("user", user);
            session.setAttribute("projects", projectService.getProjectByUserId(user.getId()));
            if (user.getRole().equals("admin")) {
                return "redirect:/admin";
            } else {
                return "redirect:/dashboard";
            }

        } catch (LoginSampleException e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        List<InvitationDTO> invitations = invitationService.getInvitationsByUserId(user.getId());
        model.addAttribute("invitationsDTO", invitations);
        model.addAttribute("user", user);
        model.addAttribute("projects", projectService.getProjectByUserId(user.getId()));
        model.addAttribute("userSubtasks", subtaskService.getSubtasksByUserId(user.getId()));

        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("createUser", new User());


        //Redirects to dashboard if user is not admin
        if (!user.getRole().equals("admin")) {
            return "redirect:/dashboard";
        }

        return "admin";
    }

    @PostMapping("/admin")
    public String createUser(@ModelAttribute User form, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        userService.createUser(form);

        return "redirect:/admin";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/organisations")
    public String organisations(Model model, HttpSession session) {
        // Redirects to login site if user is not logged in
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        return "organisations";
    }

}
