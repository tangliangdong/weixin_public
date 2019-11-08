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
}
