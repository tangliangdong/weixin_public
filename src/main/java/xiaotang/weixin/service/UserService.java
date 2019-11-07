package xiaotang.weixin.service;
import xiaotang.weixin.dao.UserMapper;
import xiaotang.weixin.model.User;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
* @ClassName: UserService
* @Description:
* @author tangliangdong
* @date 2019-11-7
*/
@Service("userService")
public class UserService{
    @Autowired
    private UserMapper userMapper;

    //---------增删改查基础部分S--------
    //保存
    public Integer save(User user){
        return userMapper.insert(user);
    }
    //根据id删除条目
    public Integer deleteById(String  uuid){
        User user = new User();
        user.setUuid(uuid);
        return userMapper.delete(user);
    }
    //根据主键查询
    public User selectByUuid(String uuid){
        if(!StringUtils.hasText(uuid)){
        return null;
        }
        User user = new User();
        user.setUuid(uuid);
        return userMapper.selectOne(user);
    }
    //根据传入条件查询列表
    public List<User> selectByService(User user){
        return userMapper.select(user);
    }
    //根据传入值更新，空则不变
    public Integer updateByIdSelective(User user){
        return userMapper.updateByPrimaryKeySelective(user);
    }
    //更新所有信息
    public Integer updateById(User user){
        return userMapper.updateByPrimaryKey(user);
    }
    //---------增删改查基础部分E--------
}