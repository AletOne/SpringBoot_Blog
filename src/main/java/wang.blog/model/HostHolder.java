package wang.blog.model;


import org.springframework.stereotype.Component;

/**
 * 用户拦截model
 *
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUsers(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }

}
