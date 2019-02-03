package wang.blog.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wang.blog.Application;
import wang.blog.model.User;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UserDAOTest {

    @Autowired
    private UserDAO userDAO;


    @Test
    public void insertUser() {
        User user = new User();

    }

    @Test
    public void selectById() {
    }

    @Test
    public void selectByName() {
    }

    @Test
    public void deleteById() {
    }
}