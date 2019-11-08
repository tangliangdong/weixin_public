package xiaotang.weixin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gonghui.pay.common.emun.LogTemplate;
import com.gonghui.pay.common.utils.HttpSender;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import xiaotang.weixin.dto.AccessTokenDto;
import xiaotang.weixin.dto.TextMessage;
import xiaotang.weixin.model.AccessToken;
import xiaotang.weixin.state.VisitInfo;
import xiaotang.weixin.util.MessageUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 10:32
 */
@Service("WeiXinService")
public class WeiXinService {

    public final static String WEIXIN_ACCESS_TOKEN = "WEIXIN_ACCESS_TOKEN";
    public final static String WEIXIN_EXPIRE = "WEIXIN_EXPIRE";
    public final static String WEIXIN_ACCESS_TOKEN_CTIME = "WEIXIN_ACCESS_TOKEN_CTIME";

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RedisTemplate<String,String> stringRedisTemplate;

    // 获取accessToken
    public AccessTokenDto getAccessToken(){
        AccessTokenDto accessTokenDto = accessTokenService.getOneAccessToken();
        int nowTime = (int)System.currentTimeMillis() / 1000;
        if(accessTokenDto != null){ // redis中存储了accessToken
            if(nowTime - accessTokenDto.getCtime() < 7200){ // accessToken还有效
                return accessTokenDto;
            }
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("grant_type", "client_credential");
        map.put("appid", VisitInfo.appid);
        map.put("secret", VisitInfo.secret);
        String result = HttpSender.get(VisitInfo.getAccessTokenUrl, map);
        Map<String, Object> data = (Map<String, Object>)JSONObject.parse(result);
        LogTemplate.LogForInfo("获取微信公众号的ACCESS_TOKEN：" + data);
        stringRedisTemplate.opsForValue().set(WEIXIN_ACCESS_TOKEN, (String) data.get("access_token"));
        stringRedisTemplate.opsForValue().set(WEIXIN_ACCESS_TOKEN_CTIME, String.valueOf(nowTime));
        stringRedisTemplate.opsForValue().set(WEIXIN_EXPIRE, String.valueOf(data.get("expires_in")));
        accessTokenDto = new AccessTokenDto();
        accessTokenDto.setAccessToken((String) data.get("access_token"));
        accessTokenDto.setExpire((int)data.get("expires_in"));
        accessTokenDto.setCtime(nowTime);
        return accessTokenDto;
    }

    public String newMessageRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 消息内容
            String content = requestMap.get("Content");
            LogTemplate.LogForInfo("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑
                /*if(content.equals("xxx")){

                }*/
                //自动回复
                TextMessage text = new TextMessage();
                text.setContent("霍霍哈嘿"+content);
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(System.currentTimeMillis()/1000);
                text.setMsgType(msgType);
                respMessage = MessageUtil.textMessageToXml(text);
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //文本消息
                    TextMessage text = new TextMessage();
                    text.setContent("我不管，我最美！！");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis() / 1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }
                // 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return respMessage;
    }

}
