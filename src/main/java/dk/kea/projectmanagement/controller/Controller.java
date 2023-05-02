package dk.kea.projectmanagement.controller;

import dk.kea.projectmanagement.repository.DBRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {
    DBRepository repository = new DBRepository();

    @GetMapping({"/",""})
    public String index(){
        return "index";
    }

    @GetMapping("/template")
    public String template(Model model){
        model.addAttribute("users", repository.getAllUsers());
        return "template";
    }
}
