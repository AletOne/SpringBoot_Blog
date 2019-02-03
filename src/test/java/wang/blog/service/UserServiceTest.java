package wang.blog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void register() {
        Map<String, String> map = userService.register("wang2", "123.321");
        for(String key : map.keySet()){
            System.out.print(key + ":  ");
            System.out.println(map.get(key));
        }
    }

    @Test
    public void login() {
        Map<String, String> map = userService.login("wang", "123.321");
        for(String key : map.keySet()){
            System.out.print(key + ":  ");
            System.out.println(map.get(key));
        }
    }

    @Test
    public void addLoginTicket(){
        String ticket = userService.addLoginTicket(3);
        System.out.println(ticket);
    }
}