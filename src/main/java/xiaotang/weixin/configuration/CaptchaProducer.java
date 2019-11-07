package xiaotang.weixin.configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaProducer extends DefaultKaptcha {
}