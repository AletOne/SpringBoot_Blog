package wang.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import wang.blog.model.*;
import wang.blog.service.ArticleService;
import wang.blog.service.JedisService;
import wang.blog.service.TagService;
import wang.blog.util.JBlogUtil;
import wang.blog.util.RedisKeyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private TagService tagService;
    @Autowired
    private JedisService jedisService;

    @RequestMapping(path="page/{pageId}")
    public String article(Model model, @PathVariable("pageId")int pageId){
        List<ViewObject> vos = new ArrayList<>();
        List<Article> articles = articleService.getLatestArticles((pageId - 1) * 4, 4);
        for(Article article : articles){
            ViewObject viewObject = new ViewObject();
            List<Tag> tags = tagService.getTagByArticleId(article.getId());
            String clickCount = "0";
            viewObject.set("clickCount", clickCount);
            viewObject.set("article", article);
            viewObject.set("tags", tags);
            vos.add(viewObject);
        }
        model.addAttribute("vos", vos);

        ViewObject pagination = new ViewObject();
        int count = articleService.getArticleCount();
        pagination.set("current", pageId);
        pagination.set("nextPage", pageId + 1);
        pagination.set("prePage", pageId - 1);
        pagination.set("lastPage", count/4+1);

        User user = hostHolder.getUser();
        if(user == null || !"admin".equals(user.getRole())){
            model.addAttribute("create", -1);
        }else{
            model.addAttribute("create", 1);
        }

        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("pagination", pagination);


        ViewObject categoryCount = new ViewObject();
        for (String category: JBlogUtil.category){
            String categoryKey = RedisKeyUtil.getCategoryKey(category);
            String num = jedisService.get(categoryKey);
            if (num!=null)
                categoryCount.set(JBlogUtil.categoryMap.get(category),num);
            else
                categoryCount.set(JBlogUtil.categoryMap.get(category),0);
        }
        model.addAttribute("categoryCount",categoryCount);

        ViewObject clickCount = new ViewObject();
        String currentPage = jedisService.get(RedisKeyUtil.getClickCountKey("/page/"+pageId));
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

    @RequestMapping("/addArticle")
    public String addArticle(@RequestParam("title") String tile, @RequestParam("category") String category,
                             @RequestParam("tag") String tag, @RequestParam("description") String description,
                             @RequestParam("content") String content){
        Article article = new Article();
        article.setCategory(category);
        article.setContent(JBlogUtil.tranfer(content));
        article.setDescription(description);
        article.setTitle(tile);
        article.setCreatedDate(new Date());
        article.setCommentCount(0);

        articleService.addArticle(article);
        int articleId = article.getId();
        System.out.println("insert new article: " + articleId);
        String[] tags = tag.split(",");
        for(String t : tags){
            Tag tag1 = tagService.selectByName(t);
            if(tag1 == null){
                tag1 = new Tag();
                tag1.setName(t);
                tag1.setCount(1);
                tagService.addTag(tag1);
                int tagId = tag1.getId();

                        ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tagId);
                articleTag.setArticleId(articleId);
                tagService.addArticleTag(articleTag);
            }else{
                tagService.updateCount(tag1.getId(), tag1.getCount() + 1);

                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tag1.getId());
                articleTag.setArticleId(articleId);
                tagService.addArticleTag(articleTag);
            }
        }

        String categoryKey = RedisKeyUtil.getCategoryKey(category);
        jedisService.incr(categoryKey);
        return "redirect:/";
    }

    @RequestMapping(value = "/category/{categoryName}", method = RequestMethod.GET)
    public String category(Model model, @PathVariable("categoryName")String categoryName,
                           @RequestParam("pageId")int pageId){
        List<Article> articles = articleService.getArticlesByCategory(categoryName, (pageId - 1) * 4, 4);
        List<ViewObject> vos = new ArrayList<>();
        for(Article article : articles){
            ViewObject vo = new ViewObject();
            List<Tag> tags = tagService.getTagByArticleId(article.getId());
            String clickCount = jedisService.get(RedisKeyUtil.getClickCountKey("/article/" + article.getId()));
            if(clickCount == null){
                clickCount = "0";
            }
            vo.set("clickCount", clickCount);
            vo.set("article", article);
            vo.set("tags", tags);
            vos.add(vo);
        }
        model.addAttribute("vos", vos);

        ViewObject pagination = new ViewObject();
        int count = articleService.getArticleCountByCategory(categoryName);

        pagination.set("current", pageId);
        pagination.set("nextPage", pageId + 1);
        pagination.set("prePage", pageId - 1);
        pagination.set("lastPage", count/4+1);
        model.addAttribute("pagination", pagination);

        User user = hostHolder.getUser();
        if(user == null || !"admin".equals(user.getRole())){
            System.out.println("need authority");
            model.addAttribute("create", -1);
        }else{
            System.out.println(user.getRole());
            model.addAttribute("create", 1);
        }
        model.addAttribute("username", user == null ? "" : user.getName());

        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("category", categoryName);

        ViewObject categoryCount = new ViewObject();
        for (String category: JBlogUtil.category){
            String categoryKey = RedisKeyUtil.getCategoryKey(category);
            String num = jedisService.get(categoryKey);
            if (num!=null)
                categoryCount.set(JBlogUtil.categoryMap.get(category),num);
            else
                categoryCount.set(JBlogUtil.categoryMap.get(category),0);
        }
        model.addAttribute("categoryCount",categoryCount);

        ViewObject clickCount = new ViewObject();
        String currentPage = jedisService.get(RedisKeyUtil.getClickCountKey("/category/"+categoryName));
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

        return "category";
    }

    @RequestMapping("/article/{articleId}")
    public String singleArticle(Model model, @PathVariable("articleId")int articleId){
        Article article = articleService.getArticleById(articleId);
        List<Tag> tags = tagService.getTagByArticleId(articleId);
        model.addAttribute("article", article);
        model.addAttribute("tags", tags);
        User user = hostHolder.getUser();
        if(user != null){
            model.addAttribute("username", user.getName());
        }

        if(user == null || !"admin".equals(user.getRole())){
            System.out.println("need authority");
            model.addAttribute("create", -1);
        }else{
            System.out.println(user.getRole());
            model.addAttribute("create", 1);
        }



        ViewObject clickCount = new ViewObject();
        String currentPage = jedisService.get(RedisKeyUtil.getClickCountKey("/article/"+articleId));
        String sumPage = jedisService.get(RedisKeyUtil.getClickCountKey("SUM"));
        clickCount.set("currentPage",currentPage);
        clickCount.set("sumPage",sumPage);
        model.addAttribute("clickCount",clickCount);

        String articleClickCount = jedisService.get(RedisKeyUtil.getClickCountKey("/article/"+article.getId()));
        if (articleClickCount==null){
            articleClickCount = "0";
        }

        model.addAttribute("articleClickCount",articleClickCount);

        return "article";
    }

    @RequestMapping(value = "/tag/{tagId}", method = RequestMethod.GET)
    public String  tag(Model model, @PathVariable("tagId") int tagId, @RequestParam("pageId")int pageId){
        List<Article> articles = articleService.getArticleByTag(tagId, (pageId - 1) * 4, 4);
        List<ViewObject> vos = new ArrayList<>();
        for(Article article : articles){
            ViewObject vo = new ViewObject();
            List<Tag> tags = tagService.getTagByArticleId(article.getId());
            String clickCount = jedisService.get(RedisKeyUtil.getClickCountKey("/article/" + article.getId()));
            System.out.println("click " + clickCount);
            if(clickCount == null){
                clickCount = "0";
            }
            vo.set("clickCount", clickCount);
            vo.set("article", article);
            vo.set("tags", tags);
            vos.add(vo);
        }
        model.addAttribute("vos", vos);

        ViewObject pagination = new ViewObject();
        int count = articleService.getArticleCountByTag(tagId);

        pagination.set("current", pageId);
        pagination.set("nextPage", pageId + 1);
        pagination.set("prePage", pageId - 1);
        pagination.set("lastPage", count / 4 + 1);


        //authority check
        User user = hostHolder.getUser();
        if(user == null || !"admin".equals(user.getRole())){
            System.out.println("need authority");
            model.addAttribute("create", -1);
        }else{
            System.out.println(user.getRole());
            model.addAttribute("create", 1);
        }

        model.addAttribute("username", user == null ? "" : user.getName());

        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("pagination", pagination);
        model.addAttribute("tagId", tagId);


        ViewObject categoryCount = new ViewObject();
        for (String category: JBlogUtil.category){
            String categoryKey = RedisKeyUtil.getCategoryKey(category);
            String num = jedisService.get(categoryKey);
            if (num!=null)
                categoryCount.set(JBlogUtil.categoryMap.get(category),num);
            else
                categoryCount.set(JBlogUtil.categoryMap.get(category),0);
        }
        model.addAttribute("categoryCount",categoryCount);

        ViewObject clickCount = new ViewObject();
        String currentPage = jedisService.get(RedisKeyUtil.getClickCountKey("/tag/"+tagId));
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

        return "tag";
    }


}
