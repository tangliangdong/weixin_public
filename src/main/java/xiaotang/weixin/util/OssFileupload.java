package xiaotang.weixin.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.gonghui.pay.common.exceptions.ValidationException;
import com.gonghui.pay.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/28.
 */
@Component
public class OssFileupload {

    @Value("${oss.backAccessKeyId}")
    private String accessKeyId;
    @Value("${oss.backAccessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.stsRoleArn}")
    private String roleArn;
    @Value("${oss.dirRoot}")
    private String DIR_ROOT;
    @Value("${oss.bucket}")
    public String BUCKET;
    @Value("${oss.endpoint}")
    public String ENDPOINT;

    public static String domain = "https://res.gongfupay.com/";

    /**
     * @param inputStream 文件流
     * @param fileName    要存放的文件路径
     * @throws IOException
     */
    public String doOssFileUpload(InputStream inputStream, String fileName) {
        OSSClient ossClient = new OSSClient(ENDPOINT, accessKeyId, accessKeySecret);
        String dir = DIR_ROOT+ DateUtil.getTodayYearStr()+"/"+DateUtil.getTodayMonthStr()+"/"+DateUtil.getTodayStr()+"/";
        PutObjectResult result = ossClient.putObject(BUCKET, dir + fileName, inputStream, null);
        ossClient.shutdown();
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ValidationException("关闭流失败");
        }
        return domain + dir + fileName;
    }




}
