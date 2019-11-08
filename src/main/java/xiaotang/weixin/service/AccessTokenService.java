package xiaotang.weixin.service;
import com.google.common.base.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import xiaotang.weixin.dao.AccessTokenMapper;
import xiaotang.weixin.dto.AccessTokenDto;
import xiaotang.weixin.model.AccessToken;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
* @ClassName: AccessTokenService
* @Description:
* @author tangliangdong
* @date 2019-11-8
*/
@Service("accessTokenService")
public class AccessTokenService{

    @Autowired
    private RedisTemplate<String,String> stringRedisTemplate;

    public AccessTokenDto getOneAccessToken(){
        String accessToken = stringRedisTemplate.opsForValue().get(WeiXinService.WEIXIN_ACCESS_TOKEN);
        if(Strings.isNullOrEmpty(accessToken)){
            return null;
        }
        String accessTokenCtime = stringRedisTemplate.opsForValue().get(WeiXinService.WEIXIN_ACCESS_TOKEN_CTIME);
        String expire = stringRedisTemplate.opsForValue().get(WeiXinService.WEIXIN_EXPIRE);

        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setAccessToken(accessToken);
        accessTokenDto.setCtime(Integer.valueOf(accessTokenCtime));
        accessTokenDto.setExpire(Integer.valueOf(expire));
        return accessTokenDto;
    }
}