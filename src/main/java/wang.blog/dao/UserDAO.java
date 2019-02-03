package wang.blog.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import wang.blog.model.User;

@Repository
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = "name,password,salt,head_url,role";
    String SELECT_FIELDS = "id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ")values (#{name},#{password},#{salt},#{headUrl},#{role})"})
    void insertUser(User user);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id=#{id}"})
    User selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where name=#{name}"})
    User selectByName(String name);

    @Delete({"delete from", TABLE_NAME, "where id=#{id}"})
    void deleteById(int id);

}
