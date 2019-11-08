/**
*@Author: tangliangdong
*@Date: 2019-11-8
*/
package xiaotang.weixin.model;
import com.gonghui.pay.common.mybatis.annotation.UUID;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.Date;
import com.gonghui.pay.common.mybatis.annotation.CreateTime;
import java.util.Date;
import com.gonghui.pay.common.mybatis.annotation.UpdateTime;
/**
* @ClassName: AccessToken
* @Description:
* @author tangliangdong
* @date 2019-11-8
*/
public class AccessToken{
	
	//
	@UUID
    @Id
    @Column(name="uuid")
	private String uuid;
	//
    @Column(name="access_token")
	private String accessToken;
	//
    @Column(name="expire")
	private String expire;
	//
    @Column(name="status")
	private Short status;
	//
    @CreateTime
    @Column(name="ctime")
	private Date ctime;
	//
    @UpdateTime
    @Column(name="utime")
	private Date utime;


	public void setUuid(String value) {
		this.uuid = value;
	}
	public String getUuid() {
		return this.uuid;
	}

	public void setAccessToken(String value) {
		this.accessToken = value;
	}
	public String getAccessToken() {
		return this.accessToken;
	}

	public void setExpire(String value) {
		this.expire = value;
	}
	public String getExpire() {
		return this.expire;
	}

	public void setStatus(Short value) {
		this.status = value;
	}
	public Short getStatus() {
		return this.status;
	}

	public void setCtime(Date value) {
		this.ctime = value;
	}
	public Date getCtime() {
		return this.ctime;
	}

	public void setUtime(Date value) {
		this.utime = value;
	}
	public Date getUtime() {
		return this.utime;
	}
}

