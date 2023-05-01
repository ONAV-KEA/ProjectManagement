package dk.kea.projectmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping({"/",""})
    public String index(){
        return "index";
    }

    @GetMapping("/template")
    public String template(){
        return "template";
    }
}
