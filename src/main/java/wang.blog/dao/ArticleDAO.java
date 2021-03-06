package wang.blog.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import wang.blog.model.Article;

import java.util.List;
 
@Repository
@Mapper
public interface ArticleDAO {
    String TABLE_NAME = "article";
    String INSERT_FIELDS = "title, description, content, created_date, comment_count, category";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ")", "values (#{title}, #{description}, #{content}, #{createdDate}, #{commentCount}, #{category})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertArticle(Article article);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    Article selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "order by id desc limit #{offset}, #{limit}"})
    List<Article> selectLatestArticle(@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where category=#{category} order by id desc limit #{offset},#{limit}"})
    List<Article> selectArticlesByCategory(@Param("category") String category,@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from", TABLE_NAME})
    int countOfArticles();

    @Select({"select count(id) from", TABLE_NAME, "where category=#{category}"})
    int getCountByCategory(@Param("category") String category);


    @Update({"update",TABLE_NAME,"set comment_count = #{commentCount} where id = #{questionId}"})
    void updateCommentCount(@Param("questionId") int questionId,@Param("commentCount") int commentCount);

    @Delete({"delete from",TABLE_NAME,"where id=#{id}"})
    void deleteById(int id);
}
