package wang.blog.controller;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import wang.blog.model.Article;
import wang.blog.model.HostHolder;
import wang.blog.model.User;
import wang.blog.model.ViewObject;
import wang.blog.service.ArticleService;
import wang.blog.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/", "/index"})
    public String index(Model model){
        List<ViewObject> vos = new ArrayList<>();
        List<Article> articles = articleService.getLatestArticles(0, 4);
        for(Article article : articles){
            ViewObject o = new ViewObject();
            o.set("article", article);
            vos.add(o);
        }
        ViewObject pagination = new ViewObject();
        int count = articleService.getArticleCount();
        User user = hostHolder.getUser();
        if(user == null || "admin".equals(user.getRole())){
            model.addAttribute("create", 1);
        }else{
            model.addAttribute("create", 0);
        }

        pagination.set("current", 1);
        pagination.set("nextPage", 2);
        pagination.set("lastPage", count/4 + 1);
        model.addAttribute("pagination", pagination);
        return "index";
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

    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping("/create")
    public String create(Model model){
        User user = hostHolder.getUser();
        if (user==null||"admin".equals(user.getRole())){
            model.addAttribute("create",1);
        }else {
            model.addAttribute("create",0);
        }
        return "create";
    }

    @RequestMapping("/about")
    public String about(Model model){
//
//        ViewObject clickCount = new ViewObject();
//        String currentPage = jedisService.get(RedisKeyUntil.getClickCountKey("/about"));
//        String sumPage = jedisService.get(RedisKeyUntil.getClickCountKey("SUM"));
//        clickCount.set("currentPage",currentPage);
//        clickCount.set("sumPage",sumPage);
//        model.addAttribute("clickCount",clickCount);

        return "about";
    }

}
