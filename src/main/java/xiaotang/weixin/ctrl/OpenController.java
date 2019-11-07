package xiaotang.weixin.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaotang.weixin.model.User;
import xiaotang.weixin.service.UserService;

import java.util.List;

@RestController
@RequestMapping("open")
public class OpenController {

    @Autowired
    private UserService userService;

    @PostMapping("test")
    public List<User> test(String username){
        User user = new User();
        user.setUsername(username);
        List<User> list = userService.selectByService(user);
        return list;
    }

}
