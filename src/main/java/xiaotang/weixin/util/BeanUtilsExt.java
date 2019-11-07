package xiaotang.weixin.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  探路者 2015/7/21
 */
public abstract class BeanUtilsExt extends BeanUtils {

    public static void copyIncludeProperties(Object source, Object target,String... includeProperties){
        copyIncludeProperties(source,target,null,includeProperties);
    }

    public static <T> T  convertUrlParameter(String urlParameter,Class<T> tClass){
        Map<String,Object> map = new HashMap<>();
        List<String> keyValues = Arrays.asList(urlParameter.split("&"));
        if(!CollectionUtils.isEmpty(keyValues)){
            for (String keyValue : keyValues) {
                List<String> tt =  Arrays.asList(keyValue.split("="));
                if(!CollectionUtils.isEmpty(tt)){
                    if(tt.size()==2){
                        map.put(tt.get(0),tt.get(1));
                    }else{
                        map.put(tt.get(0),null);
                    }
                }
            }
        }
        return mapConvertBean(tClass, map);
    }

    private static <T> T mapConvertBean(Class<T> tClass, Map<String, Object> map){
        BeanInfo beanInfo = null; // 获取类属性
        T obj = null;
        try {
            beanInfo = Introspector.getBeanInfo(tClass);
            obj = tClass.newInstance();
            PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
            for (int i = 0; i< propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static void copyIncludeProperties(Object source, Object target, Class<?> editable, String... includeProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                                                   "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (includeProperties != null ? Arrays.asList(includeProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList != null || ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                        ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                                readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}
