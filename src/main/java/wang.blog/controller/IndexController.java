package wang.blog.controller;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.css.ViewCSS;
import redis.clients.jedis.Jedis;
import wang.blog.model.*;
import wang.blog.service.ArticleService;
import wang.blog.service.JedisService;
import wang.blog.service.TagService;
import wang.blog.service.UserService;
import wang.blog.util.JBlogUtil;
import wang.blog.util.RedisKeyUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private TagService tagService;

    @Autowired
    private JedisService jedisService;

    @RequestMapping(path = {"/", "/index"})
    public String index(Model model){
        User user = hostHolder.getUser();
        if(user == null){
            return "login";
        }


        List<ViewObject> vos = new ArrayList<>();
        List<Article> articles = articleService.getLatestArticles(0, 4);
        //add articles to view object list
        for(Article article : articles){
            ViewObject o = new ViewObject();
            List<Tag> tags = tagService.getTagByArticleId(article.getId());

            String clickCount = jedisService.get(RedisKeyUtil.getClickCountKey("/article/" + article.getId()));
            System.out.println("click: " + clickCount);
            if(clickCount == null){
                clickCount = "0";

            }
            o.set("article", article);
            o.set("clickCount", clickCount);
            o.set("tags", tags);
            vos.add(o);
        }
        model.addAttribute("vos", vos);

        //add tags
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);

        ViewObject pagination = new ViewObject();
        int count = articleService.getArticleCount();

        if(!"admin".equals(user.getRole())){
            System.out.println("need authority");
            model.addAttribute("create", -1);
        }else{
            System.out.println(user.getRole());
            model.addAttribute("create", 1);
        }
        model.addAttribute("username", user.getName());
        pagination.set("current", 1);
        pagination.set("nextPage", 2);
        pagination.set("lastPage", count/4 + 1);
        model.addAttribute("pagination", pagination);

        ViewObject categoryCount = new ViewObject();
        for(String category : JBlogUtil.category){
            String categoryKey = RedisKeyUtil.getCategoryKey(category);
            String num = jedisService.get(categoryKey);
            if(num != null){
                categoryCount.set(JBlogUtil.categoryMap.get(category), num);
            }else{
                categoryCount.set(JBlogUtil.categoryMap.get(category), 0);
            }
        }
        model.addAttribute("categoryCount", categoryCount);

        ViewObject clickCount = new ViewObject();
        String countStr1 = jedisService.get(RedisKeyUtil.getClickCountKey("/"));
        String countStr2 = jedisService.get(RedisKeyUtil.getClickCountKey("/index"));
        String countStr3 = jedisService.get(RedisKeyUtil.getClickCountKey("/page/1"));
        String currentPage = String.valueOf(Integer.parseInt(countStr1==null?"0":countStr1)
                + Integer.parseInt(countStr2==null?"0":countStr2)+ Integer.parseInt(countStr3==null?"0":countStr3));
        String sumPage = jedisService.get(RedisKeyUtil.getClickCountKey("SUM"));
        clickCount.set("currentPage",currentPage);
        clickCount.set("sumPage",sumPage);
        model.addAttribute("clickCount",clickCount);

        List<Article> hotArticles = new ArrayList<>();
        Set<String> set = jedisService.zrevrange("hotArticles",0,6);
        for (String str : set){
            int articleId = Integer.parseInt(str.split(":")[1]);
            Article article = articleService.getArticleById(articleId);
            hotArticles.add(article);
        }
        model.addAttribute("hotArticles",hotArticles);

        return "index";
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
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
        System.out.println("create is called");
        User user = hostHolder.getUser();
        if (user==null||"admin".equals(user.getRole())){
            model.addAttribute("create",1);
        }else {
            model.addAttribute("create",0);
        }

        model.addAttribute("username", user.getName());
        return "create";
    }

    @RequestMapping("/about")
    public String about(Model model){

        ViewObject clickCount = new ViewObject();
        String currentPage = jedisService.get(RedisKeyUtil.getClickCountKey("/about"));
        String sumPage = jedisService.get(RedisKeyUtil.getClickCountKey("SUM"));
        clickCount.set("currentPage",currentPage);
        clickCount.set("sumPage",sumPage);
        model.addAttribute("clickCount",clickCount);

        return "about";
    }

}
