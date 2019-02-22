package wang.blog.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import wang.blog.interceptor.LoginRequestInterceptor;
import wang.blog.interceptor.PassportInterceptor;

@Component
public class WebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private PassportInterceptor passportInterceptor;

    @Autowired
    private LoginRequestInterceptor loginRequestInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequestInterceptor).addPathPatterns("/create");
        super.addInterceptors(registry);
    }

}
