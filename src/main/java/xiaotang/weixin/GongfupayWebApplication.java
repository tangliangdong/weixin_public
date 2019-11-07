package xiaotang.weixin;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import xiaotang.weixin.configuration.CaptchaProducer;
import xiaotang.weixin.interceptor.LoginInterceptor;
import xiaotang.weixin.interceptor.PageInterceptor;
import com.google.code.kaptcha.util.Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.Properties;

@SpringBootApplication
@EnableScheduling
@ComponentScan(value = {"xiaotang.weixin", "com.gonghui.pay"},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.CUSTOM,
                        classes = {TypeExcludeFilter.class}
                )}
)
@MapperScan("xiaotang.weixin.dao")
//@ImportResource({"classpath:dubbo.xml"})
@ServletComponentScan
public class GongfupayWebApplication extends WebMvcConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run( GongfupayWebApplication.class, args );
  }

  @Bean
  public LoginInterceptor loginInterceptor() {
    return new LoginInterceptor();
  }


  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor( loginInterceptor() ).addPathPatterns( "/**" )
            .excludePathPatterns( "/resources/static" )
            .excludePathPatterns( "/v2/api-docs" )
            .excludePathPatterns( "/swagger-resources/configuration/security" )
            .excludePathPatterns( "/swagger-resources" )
            .excludePathPatterns( "/swagger-resources/configuration/ui" );
    registry.addInterceptor( new PageInterceptor() ).addPathPatterns( "/**" );
    super.addInterceptors( registry );
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    super.configureMessageConverters( converters );
    FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(
            SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat
    );
    fastConverter.setFastJsonConfig( fastJsonConfig );
    converters.add( fastConverter );
  }

  @Bean
  public CaptchaProducer captchaProducer() {
    CaptchaProducer captchaProducer = new CaptchaProducer();
    Properties properties = new Properties();
    properties.setProperty( "kaptcha.border", "yes" );
    properties.setProperty( "kaptcha.border.color", "255,250,250" );
    properties.setProperty( "kaptcha.textproducer.font.color", "blue" );
    properties.setProperty( "kaptcha.image.width", "132" );
    properties.setProperty( "kaptcha.image.height", "48" );
    properties.setProperty( "kaptcha.textproducer.font.size", "45" );
    properties.setProperty( "kaptcha.session.key", "code" );
    properties.setProperty( "kaptcha.textproducer.char.length", "4" );
    properties.setProperty( "kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑" );
    Config config = new Config( properties );
    captchaProducer.setConfig( config );
    return captchaProducer;
  }
}
