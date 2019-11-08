package xiaotang.weixin.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 15:11
 */
@Setter
@Getter
public class Article {
    /**
     * 图文消息描述
     */
    private String Description;

    /**
     * 图片链接，支持JPG、PNG格式，<br>
     * 较好的效果为大图640*320，小图80*80
     */
    private String PicUrl;

    /**
     * 图文消息名称
     */
    private String Title;

    /**
     * 点击图文消息跳转链接
     */
    private String Url;

}