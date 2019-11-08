package xiaotang.weixin.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaotang.weixin.dto.AccessTokenDto;
import xiaotang.weixin.model.User;
import xiaotang.weixin.service.UserService;
import xiaotang.weixin.service.WeiXinService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("open")
public class OpenController {

    @Autowired
    private WeiXinService weiXinService;

    @PostMapping("getAccessToken")
    public AccessTokenDto test(){
        return weiXinService.getAccessToken();
    }



}
