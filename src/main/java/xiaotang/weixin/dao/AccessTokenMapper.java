package xiaotang.weixin.dao;
import xiaotang.weixin.model.AccessToken;
import tk.mybatis.mapper.common.Mapper;
import org.springframework.stereotype.Repository;

/**
* @ClassName: AccessTokenMapper
* @Description:
* @author tangliangdong
* @date 2019-11-8
*/
public interface AccessTokenMapper extends Mapper<AccessToken>{

    AccessToken selectOneAccessToken();
}