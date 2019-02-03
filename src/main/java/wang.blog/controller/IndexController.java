package wang.blog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping(path = {"/", "/index"})
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password){
        System.out.println("login was called.");
        System.out.println(username + ": " + password);
        return "redirect:/";
    }
}
