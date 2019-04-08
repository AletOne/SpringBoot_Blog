package wang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.blog.dao.ArticleDAO;
import wang.blog.model.Article;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleDAO articleDAO;

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

    public List<Article> getArticlesByCategory(String category){
        return articleDAO.selectArticlesByCategory(category);
    }

    public void updateCommentCount(int id, int count){
        articleDAO.updateCommentCount(id, count);
    }
}
