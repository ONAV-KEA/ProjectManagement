package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.DBRepository;
import dk.kea.projectmanagement.utility.LoginSampleException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@org.springframework.stereotype.Controller
public class Controller {
    DBRepository repository = new DBRepository();

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

    @GetMapping("/template")
    public String template(Model model){
        model.addAttribute("users", repository.getAllUsers());
        return "template";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("projects", repository.getProjectByUserId(user.getId()));

        // Redirects to login site if user is not logged in
        if (user.getId() == 0){
            return "redirect:/";
        }

        return "dashboard";
    }
}
