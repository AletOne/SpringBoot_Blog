package wang.blog.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import wang.blog.interceptor.ArticleClickInterceptor;
import wang.blog.interceptor.LoginRequestInterceptor;
import wang.blog.interceptor.PassportInterceptor;

/**
 * configure passport interceptor
 */
@Component
public class WebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private PassportInterceptor passportInterceptor;

    @Autowired
    private LoginRequestInterceptor loginRequestInterceptor;

    @Autowired
    private ArticleClickInterceptor articleClickInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequestInterceptor).addPathPatterns("/create");
        registry.addInterceptor(articleClickInterceptor).addPathPatterns("/article/*");
        super.addInterceptors(registry);
    }

}
