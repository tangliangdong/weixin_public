package xiaotang.weixin.interceptor;

import com.alibaba.fastjson.JSON;
import xiaotang.weixin.ResponseResultAdvice;
import xiaotang.weixin.util.UserContext;
import xiaotang.weixin.util.StaticVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * 拦截登录请求地址
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    static final List<String> NOT_NEED_LOGIN_MODULES = new ArrayList<>();
    static final List<String> COMPANY_MODULES = new ArrayList<>();
    static {
        NOT_NEED_LOGIN_MODULES.add("open");
        NOT_NEED_LOGIN_MODULES.add("index");
        NOT_NEED_LOGIN_MODULES.add("weixin");

    }

    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String[]> e:map.entrySet()){
            sb.append(e.getKey()).append("=");
            String[] value = e.getValue();
            if(value != null && value.length == 1){
                sb.append(value[0]).append("\t");
            }else{
                sb.append(Arrays.toString(value)).append("\t");
            }
        }
        return sb.toString();
    }



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserContext.setSession(request.getSession());
        Object userInfo = request.getSession().getAttribute(StaticVariable.USER_INFO);
        Object companyInfo =
                request.getSession().getAttribute(StaticVariable.COMPANY_INFO);

//        HandlerMethod handlerMethod = (HandlerMethod)handler;
//        HandlerMethod h = (HandlerMethod) handler;
//        String controller = h.getMethod().getDeclaringClass().getSimpleName();
//        Short oldApproved = -5;
//        String uuid = request.getParameter("uuid");
//        Short controllerType = 0;
//        if(uuid!=null){
//            switch (controller){
//                case "CompanyController":
//                    Company company = companyService.selectByUuid(uuid);
//                    oldApproved = company.getApproved();
//                    controllerType = 1;
//                    break;
//                case "ProjectController":
//                    Project project = projectService.selectByUuid(uuid);
//                    oldApproved = project.getApproved();
//                    controllerType = 2;
//                    break;
//            }
//        }
//        request.setAttribute("oldApproved", String.valueOf(oldApproved));
//        request.setAttribute("controllerType", controllerType);

        if(needInterceptUrl(request)){
            return true;
        }
        if (!request.getRequestURI().equals("/error")) {
            if (userInfo == null && companyInfo == null) {
                loginTimeOut(response, "登陆已过期、请重新登陆");
                return false;
            }else if (userInfo == null && companyInfo != null){
                System.out.println("供应商登陆");
                if(!companyPermitUrl(request)){
                    loginTimeOut(response, "供应商无权限");
                    return false;
                }
            }else if (userInfo != null && companyInfo == null){
                System.out.println("管理员登陆");
            }
        }
        return true;
    }


    public void loginTimeOut(HttpServletResponse response, String msg) throws IOException {
        ResponseResultAdvice.Result result = ResponseResultAdvice.Result.getInstance();
        result.setCode(-99);
        result.setMsg(msg);
        String resultJson = JSON.toJSONString(result);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(resultJson);
        writer.close();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {
//        String approved = request.getParameter("approved");
////        String uuid = request.getParameter("uuid");
//        String oldApproved = (String)request.getAttribute("oldApproved");
//        Short controllerType = (Short)request.getAttribute("controllerType");
//        if (handler instanceof HandlerMethod) {
//            HttpSession httpSession = UserContext.getSession();
//            HandlerMethod h = (HandlerMethod) handler;
//            if(approved!=null&&!"".equals(approved)&&"checkCompany".equals(h.getMethod().getName())){
//                String userinfo = (String)httpSession.getAttribute(StaticVariable.USER_INFO);
//                Map<String, Object> map = JSONObject.fromObject(userinfo);
//                String account = (String)map.get("account");
//                Log log = new Log();
//                String controller = h.getMethod().getDeclaringClass().getSimpleName();
//                log.setType(controllerType);
//                log.setHandleResult(approved);
//                log.setHandleStatus(oldApproved);
//                log.setAccount(StringUtils.hasText(account)?account:"登陆用户已失效");
//                log.setStatus(Short.valueOf("1"));
//                log.setController(controller);
//                log.setMethod(h.getMethod().getName());
//                logService.save(log);
//
//            }
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {

    }

    private Boolean needInterceptUrl(HttpServletRequest request) {
        Boolean notNeed = false;
        String uri = request.getRequestURI();
        String path = request.getContextPath();
        uri = uri.replaceFirst(path, "");
        if (uri.startsWith("/")) {
            uri = uri.replaceFirst("/", "");
        }
        for (String notNeedLoginModule : NOT_NEED_LOGIN_MODULES) {
            if (uri.startsWith(notNeedLoginModule)) {
                return true;
            }
        }
        return notNeed;
    }

    private Boolean companyPermitUrl(HttpServletRequest request) {
        Boolean notNeed = false;
        String uri = request.getRequestURI();
        String path = request.getContextPath();
        uri = uri.replaceFirst(path, "");
        if (uri.startsWith("/")) {
            uri = uri.replaceFirst("/", "");
        }
        for (String companyModule : COMPANY_MODULES) {
            if (uri.startsWith(companyModule)) {
                return true;
            }
        }
        return notNeed;
    }
}
