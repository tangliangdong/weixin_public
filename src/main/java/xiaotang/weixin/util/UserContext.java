package xiaotang.weixin.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpSession;


public abstract class UserContext {
    private static final ThreadLocal<HttpSession> THREAD_LOCAL = new ThreadLocal<HttpSession>();
    /**
     * 获取属性
     * @param property
     * @param tClass
     * @param <T>
     * @return
     */
    public static final <T> T getProperty(String property, Class<T> tClass) {
        HttpSession session = THREAD_LOCAL.get();
        if (session == null) return null;
        Object obj = session.getAttribute(property);
        if (obj == null){
            return null;
        }else{
            String val = (String)obj;
            return JSON.parseObject(val, tClass);
        }
    }
    public static final void setProperty(String property,Object value){
        String valueStr = JSON.toJSONString(value);
        THREAD_LOCAL.get().setAttribute(property,valueStr);
    }
    /**
     * @param httpSession
     * @return
     */
    public static final void setSession(HttpSession httpSession){
        THREAD_LOCAL.set(httpSession);
    }

    public static final HttpSession getSession(){
        return THREAD_LOCAL.get();
    }
}
