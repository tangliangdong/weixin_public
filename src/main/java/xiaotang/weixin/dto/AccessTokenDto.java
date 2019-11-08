package xiaotang.weixin.dto;

import com.gonghui.pay.common.mybatis.annotation.CreateTime;
import com.gonghui.pay.common.mybatis.annotation.UpdateTime;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 11:09
 */
public class AccessTokenDto {

    private String accessToken;
    //
    private Integer expire;
    //
    private Integer ctime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Integer getCtime() {
        return ctime;
    }

    public void setCtime(Integer ctime) {
        this.ctime = ctime;
    }
}
