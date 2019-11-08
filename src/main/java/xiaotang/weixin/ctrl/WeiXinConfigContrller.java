package xiaotang.weixin.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaotang.weixin.service.WeiXinService;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 14:45
 */
@RestController
@RequestMapping("weixin/config")
public class WeiXinConfigContrller {

    @Autowired
    private WeiXinService weiXinService;

//    @PostMapping("setMyButton")
//    public String setMyButton(){
//        return weiXinService.setMyButton();
//    }
}
