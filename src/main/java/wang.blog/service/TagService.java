package wang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.blog.dao.ArticleTagDAO;
import wang.blog.dao.TagDAO;
import wang.blog.model.ArticleTag;
import wang.blog.model.Tag;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagDAO tagDAO;
    @Autowired
    private ArticleTagDAO articleTagDAO;

    public Tag selectByName(String name){
        return tagDAO.selectByName(name);
    }

    public List<Tag> getAllTags(){
        return tagDAO.selectAll();
    }

    public List<Tag> getTagByArticleId(int articleId){
        return articleTagDAO.selectTagByArticleId(articleId);
    }

    public int addTag(Tag tag){
        return tagDAO.insertTag(tag) > 0 ? tag.getId() : -1;
    }

    public int addArticleTag(ArticleTag articleTag){
        return articleTagDAO.insertArticleTag(articleTag);
    }

    public void updateCount(int tagId, int count){
        tagDAO.updateCount(tagId, count);
    }
}
