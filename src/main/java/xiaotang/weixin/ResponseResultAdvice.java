package xiaotang.weixin;

import com.gonghui.pay.common.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@ControllerAdvice(basePackages = "xiaotang.weixin.ctrl")
public class ResponseResultAdvice implements ResponseBodyAdvice<Object> {
    private static Logger logger = LoggerFactory.getLogger(ResponseResultAdvice.class);
    private final static int successCode = 1;//成功编码
    private final static int errorCode = 0;//验证错误编码
    private final static int sysErrorCode = -1;//系统错误编码

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Boolean isRest = AnnotationUtils.isAnnotationDeclaredLocally(
                RestController.class, returnType.getContainingClass());
        ResponseBody responseBody = AnnotationUtils.findAnnotation(
                returnType.getMethod(), ResponseBody.class);
        return isRest || responseBody != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (!(body instanceof Result)) {
            Result result = Result.getInstance();
            body = body == null ? "" : body;
            result.setResult(body);
            body = result;
        }
        return body;
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Result handleAllException(Exception ex) {
        Result result = new Result();
        String msg;
        result.setSuccess(false);
        result.setCode(errorCode);
        if (ex instanceof ValidationException) {
            ValidationException validateException = (ValidationException) ex;
            if (validateException.getCode() != null) {
                result.setCode(validateException.getCode());
            }
            msg = validateException.getMessage();
            result.setCode(validateException.getCode() == null ? errorCode : validateException.getCode());
        }  else {
            result.setCode(sysErrorCode);
            msg = "网络异常，请稍后重试";
        }
        logger.error(ex.getMessage(), ex);
        result.setMsg(msg);
        return result;
    }



    public static class Result {
        private String msg;
        private Boolean success;
        private Object result;
        private Integer code;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public static Result getInstance() {
            Result result = new Result();
            result.setSuccess(true);
            result.setMsg("操作成功");
            result.setResult("");
            result.setCode(successCode);
            return result;
        }
    }
}
