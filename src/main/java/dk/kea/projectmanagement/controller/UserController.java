package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.service.*;
import dk.kea.projectmanagement.utility.LoginSampleException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
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
    public String editUser(@PathVariable int id, @ModelAttribute("user") User form, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }

        System.out.println("Edit user form: " + form.getFirstName() + ' ' + form.getLastName() + ' ' + form.getRole() + ' ' + form.getUsername() + ' ' + form.getPassword());

        // Update the user information in the database
        User editedUser = userService.editUser(form, id);
        System.out.println("Edited user: " + editedUser + " with id: " + id);

        if (editedUser != null) {
            // Update the user information in the session, but only if the user edited himself
            User user = (User) session.getAttribute("user");
            if (user.getId() == editedUser.getId()) {
                session.setAttribute("user", editedUser);
                System.out.println("Updated user in session: " + editedUser);
            }
        }

        // Update the list of users in the session
        session.setAttribute("users", userService.getAllUsers());
        System.out.println("Updated users in session: " + userService.getAllUsers());

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

}
