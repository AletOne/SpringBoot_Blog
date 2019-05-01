package wang.blog.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import wang.blog.service.JedisService;
import wang.blog.util.RedisKeyUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ArticleClickInterceptor implements HandlerInterceptor {

    @Autowired
    private JedisService jedisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri = httpServletRequest.getServletPath().split("/")[2];
        String uriKey = RedisKeyUtil.getClickCountKey(uri);
        jedisService.zincrby("hotArticles", uriKey);
        System.out.println("increase key" + uriKey);
        jedisService.incr(RedisKeyUtil.getClickCountKey("/article/" + uri));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
