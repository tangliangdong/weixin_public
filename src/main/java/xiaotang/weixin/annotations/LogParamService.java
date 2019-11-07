package xiaotang.weixin.annotations;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Order(100)
@Component
public class LogParamService {
    public static final Logger logger =  LoggerFactory.getLogger(LogParamService.class);
    public static final String dateformat = "yyyy:MM:dd HH:mm:ss";
    public static final String STIRNG_START = "\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
    public static final String STIRNG_END = "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";


    @Pointcut("execution( * com.gonghui.monitor.realname.ctrl..*(..))")
    public void serviceLogParam(){

    }
    @Around("serviceLogParam()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // ProceedingJoinPoint 为JoinPoint 的子类，在父类基础上多了proceed()方法，用于增强切面
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //java reflect相关类，通过反射得到注解
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();

        StringBuffer classAndMethod = new StringBuffer();

        //获取类注解LogParam
        LogParam classAnnotation = targetClass.getAnnotation(LogParam.class);
        //获取方法注解LogParam
        LogParam methodAnnotation = method.getAnnotation(LogParam.class);

        //如果类上LogParam注解不为空，则执行proceed()
        if (classAnnotation != null) {
            if (classAnnotation.ignore()) {
                //proceed() 方法执行切面方法，并返回方法返回值
                return joinPoint.proceed();
            }
            classAndMethod.append(classAnnotation.value()).append("-");
        }

        //如果方法上LogParam注解不为空，则执行 proceed()
        if (methodAnnotation != null) {
            if (methodAnnotation.ignore()) {
                return joinPoint.proceed();
            }
            classAndMethod.append(methodAnnotation.value());
        }else{
            return joinPoint.proceed();
        }

        //拼凑目标类名和参数名
        String target = "【" +UUID.randomUUID()+"|"+method.getName()+"】";
        String params = JSONObject.toJSONStringWithDateFormat(joinPoint.getArgs(), dateformat, SerializerFeature.WriteMapNullValue);
        logger.info(STIRNG_START + "{} 开始调用--> {} 参数:{}", classAndMethod.toString(), target, params);

        long start = System.currentTimeMillis();
        //如果类名上和方法上都没有LogParam注解，则直接执行 proceed()
        Object result = joinPoint.proceed();
        long timeConsuming = System.currentTimeMillis() - start;

        logger.info("\n{} 调用结束<-- {}  耗时:{}ms" + STIRNG_END, classAndMethod.toString(), target,  timeConsuming);

        return result;

    }
}
