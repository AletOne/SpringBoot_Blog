package wang.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wang.blog.model.Article;
import wang.blog.service.ArticleService;
import wang.blog.util.JBlogUtil;

import java.util.Date;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/addArticle")
    public String addArticle(@RequestParam("title") String tile, @RequestParam("category") String category,
                             @RequestParam("tag") String tag, @RequestParam("description") String description,
                             @RequestParam("content") String content){
        Article article = new Article();
        article.setCategory(category);
        article.setContent(JBlogUtil.tranfer(content));
        article.setDescription(description);
        article.setTitle(tile);
        article.setCreateDate(new Date());
        article.setCommentCount(0);

        int ret = articleService.addArticle(article);
        return "redirect:/";
    }


}
