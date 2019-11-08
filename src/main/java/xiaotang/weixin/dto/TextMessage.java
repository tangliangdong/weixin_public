package xiaotang.weixin.dto;

/**
 * @author Administrator
 * @data 2019/11/8
 * @time 15:00
 */
public class TextMessage extends BaseMessage{
    // 消息内容
    private String Content;

    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    }

}