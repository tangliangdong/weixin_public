package xiaotang.weixin.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 404 重定向到 index.html 由vue 接管
 */
@Component
public class PageInterceptor extends HandlerInterceptorAdapter {
  private List<Integer> errorCodeList = Arrays.asList( 404, 403 );

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    int status = response.getStatus();
    System.out.println(status);
    if(errorCodeList.contains(status) && modelAndView !=null){
      System.out.println("404");
      modelAndView.setViewName("index.html");
    }
  }
}
