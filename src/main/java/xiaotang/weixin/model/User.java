/**
*@Author: tangliangdong
*@Date: 2019-11-7
*/
package xiaotang.weixin.model;
import com.gonghui.pay.common.mybatis.annotation.UUID;
import javax.persistence.Id;
import javax.persistence.Column;
/**
* @ClassName: User
* @Description:
* @author tangliangdong
* @date 2019-11-7
*/
public class User{
	
	//
	@UUID
    @Id
    @Column(name="uuid")
	private String uuid;
	//
    @Column(name="username")
	private String username;
	//
    @Column(name="password")
	private String password;


	public void setUuid(String value) {
		this.uuid = value;
	}
	public String getUuid() {
		return this.uuid;
	}

	public void setUsername(String value) {
		this.username = value;
	}
	public String getUsername() {
		return this.username;
	}

	public void setPassword(String value) {
		this.password = value;
	}
	public String getPassword() {
		return this.password;
	}
}

