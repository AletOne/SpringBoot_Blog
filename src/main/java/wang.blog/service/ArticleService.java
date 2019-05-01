package wang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.blog.dao.ArticleDAO;
import wang.blog.dao.ArticleTagDAO;
import wang.blog.model.Article;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleDAO articleDAO;

    @Autowired
    private ArticleTagDAO articleTagDAO;

    public int addArticle(Article article){
        return articleDAO.insertArticle(article);
    }

    public Article getArticleById(int id){
        return articleDAO.selectById(id);
    }

    public int getArticleCount(){
        return articleDAO.countOfArticles();
    }

    public int getArticleCountByCategory(String category){
        return articleDAO.getCountByCategory(category);
    }

    public List<Article> getLatestArticles(int offset, int limit){
        return articleDAO.selectLatestArticle(offset, limit);
    }

    public List<Article> getArticlesByCategory(String category, int offset, int limit){
        return articleDAO.selectArticlesByCategory(category, offset, limit);
    }

    public List<Article> getArticleByTag(int tagId, int offset, int limit){
        return articleTagDAO.selectByTagId(tagId, offset, limit);
    }

    public void updateCommentCount(int id, int count){
        articleDAO.updateCommentCount(id, count);
    }

    public int getArticleCountByTag(int tagId){
        return articleTagDAO.selectArticleCountByTagId(tagId);
    }
}
