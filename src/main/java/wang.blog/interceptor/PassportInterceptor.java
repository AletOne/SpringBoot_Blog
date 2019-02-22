package wang.blog.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import wang.blog.dao.LoginTicketDAO;
import wang.blog.dao.UserDAO;
import wang.blog.model.HostHolder;
import wang.blog.model.LoginTicket;
import wang.blog.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * 拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDAO loginTicketDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception{
        String ticket = null;
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("ticket".equals(cookie.getName())){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if(ticket != null){
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            //如果没有该ticket或者ticket已经过期了，就不把user放进hostholder中
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUsers(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o,
                           ModelAndView modelAndView) throws Exception{
        if(modelAndView != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o,
                                Exception e) throws Exception{
        hostHolder.clear();
    }

}
