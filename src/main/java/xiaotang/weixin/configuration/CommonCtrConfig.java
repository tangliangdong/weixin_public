package xiaotang.weixin.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 通用控制层映射
 *
 * @author ldd
 * @version 2.0
 * @since 2016/7/27
 */
@Configuration
public class CommonCtrConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        //首页、登录、注册
        registry.addViewController("/index").setViewName("/index");
        registry.addViewController("/e_error").setViewName("/e_error");
        registry.addViewController("/docs").setViewName("/docs");

    }
}
