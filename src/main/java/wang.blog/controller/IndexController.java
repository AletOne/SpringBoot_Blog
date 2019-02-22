package wang.blog.controller;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import wang.blog.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/", "/index"})
    public String index(){
        return "login";
    }

    @RequestMapping(value = "/login")
    public String login(Model model, HttpServletResponse httpServletResponse, @RequestParam String username, @RequestParam String password,
                        @RequestParam(value = "next",required = false)String next){

        Map<String, String> map = userService.login(username, password);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);

            if (StringUtils.isNotBlank(next)){
                return "redirect:"+next;
            }

            return "redirect:/";
        }else {
            model.addAttribute("msg", map.get("msg"));
            System.out.println("failed" + map.get("msg"));
            return "login";
        }

    }

    @RequestMapping("/in")
    public String in(Model model,@RequestParam(value = "next",required = false)String next){
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping("/register")
    public String register(Model model, HttpServletResponse httpResponse,
                           @RequestParam String username, @RequestParam String password
            ,@RequestParam(value = "next",required = false)String next){
        Map<String,String> map = userService.register(username,password);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket"));
            cookie.setPath("/");
            httpResponse.addCookie(cookie);

            if (StringUtils.isNotBlank(next))
                return "redirect:"+next;
            else
                return "redirect:/";
        }else {
            model.addAttribute("msg",map.get("msg"));
            return "login";
        }
    }
}
