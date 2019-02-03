package wang.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import wang.blog.dao.LoginTicketDAO;
import wang.blog.dao.UserDAO;
import wang.blog.model.LoginTicket;
import wang.blog.model.User;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    /**
     * register function.
     * param: String username, String password
     * @return Map<message, message content>
     */
    public Map<String, String> register(String username, String password){
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        if(username == null || username.equals("")){
            map.put("msg", "username cannot be null or empty");
            return map;
        }
        if(password == null || password.equals("")){
            map.put("msg", "password cannot be empty");
            return map;
        }
        if(userDAO == null){
            System.out.println("kong!!!");
        }

        User u = userDAO.selectByName(username);
        if(u != null){
            map.put("msg", "user existed!");
            return map;
        }

        User user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png",random.nextInt(1000)));
        user.setPassword(DigestUtils.md5DigestAsHex((password+"/" + user.getSalt()).getBytes()));
        user.setRole("user");
        userDAO.insertUser(user);

        user = userDAO.selectByName(username);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, String> login(String username, String password){
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        if(username == null || username.equals("")){
            map.put("msg", "username cannot be null or empty");
            return map;
        }
        if(password == null || password.equals("")){
            map.put("msg", "password cannot be empty");
            return map;
        }

        if(userDAO == null){
            System.out.println("kong!!!");
        }
        User u = userDAO.selectByName(username);
        if(u == null){
            map.put("msg", "user doesn't exist!");
            return map;
        }

        String md5 = DigestUtils.md5DigestAsHex((password + "/" + u.getSalt()).getBytes());
        if(!md5.equals(u.getPassword())){
            map.put("msg", "Incorrect password!");
            return map;
        }

        String ticket = addLoginTicket(u.getId());
        map.put("ticket", ticket);
        return map;
    }

    /**
     *
     * @param userId
     * @return ticket
     */
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*30);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));

        loginTicketDAO.insertLoginTicket(loginTicket);
        return loginTicket.getTicket();
    }
}
