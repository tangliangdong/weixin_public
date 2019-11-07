package xiaotang.weixin.util;

import com.gonghui.pay.enums.SMSType;

import javax.servlet.http.HttpSession;


public class SMSCodeComponent {
    /**
     * 清除短信验证码
     *
     * @param type
     * @param session
     */
    public static void smsCodeDestroy(SMSType type, HttpSession session) {
        SMSType.SMSConfigKey smsConfigKey = type.getConfigKey();
        session.removeAttribute(smsConfigKey.getCreateTimeKey());
        session.removeAttribute(smsConfigKey.getMobileKey());
        session.removeAttribute(smsConfigKey.getCodeKey());
        session.removeAttribute(smsConfigKey.getValicateTimes());
    }
}
