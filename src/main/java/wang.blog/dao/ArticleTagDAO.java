package wang.blog.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import wang.blog.model.Article;
import wang.blog.model.ArticleTag;
import wang.blog.model.Tag;

import java.util.List;

@Repository
@Mapper
public interface ArticleTagDAO {
    String TABLE_NAME = "article_tag";
    String INSERT_FIELDS = "article_id, tag_id";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    String TAG_FIELDS = "id, name, count";
    String ARTICLE_FIEDLS = "id, title, description, content, created_date, comment_count, category";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ")", "values (#{articleId}, #{tagId})"})
    int insertArticleTag(ArticleTag articleTag);

    @Select({"select", TAG_FIELDS, "from tag where id in (select tag_id from article_tag where article_id = #{articleId})"})
    List<Tag> selectTagByArticleId(int articleId);

    @Select({"select",ARTICLE_FIEDLS,"from article where id in (select article_id from article_tag where tag_id=#{tagId}) limit #{offset},#{limit}"})
    List<Article> selectByTagId(@Param("tagId") int tagId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from article where id in (select article_id from article_tag where tag_id=#{tagId})"})
    int selectArticleCountByTagId(@Param("tagId") int tagId);

    @Delete({"delete from", TABLE_NAME, "where id=#{id}"})
    void deleteById(int id);

}
