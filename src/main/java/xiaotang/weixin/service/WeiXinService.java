package xiaotang.weixin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gonghui.pay.common.utils.HttpSender;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaotang.weixin.model.AccessToken;
import xiaotang.weixin.state.VisitInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 10:32
 */
@Service("WeiXinService")
public class WeiXinService {

    @Autowired
    private AccessTokenService accessTokenService;

    // 获取accessToken
    public Map<String, Object> getAccessToken(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("grant_type", "client_credential");
        map.put("appid", VisitInfo.appid);
        map.put("secret", VisitInfo.secret);
        String result = HttpSender.get(VisitInfo.getAccessTokenUrl, map);
        Map<String, Object> data = (Map<String, Object>)JSONObject.parse(result);
        return data;
    }
}
