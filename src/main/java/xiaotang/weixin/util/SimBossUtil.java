package xiaotang.weixin.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fanzetao on 2018/1/25.
 */
public class SimBossUtil {

    static Logger logger = LoggerFactory.getLogger(SimBossUtil.class);

    static ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

    static SimpleDateFormat format_utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
    static SimpleDateFormat format_display = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * flow 单位字节
     * @param flow
     * @return
     */
    public static String formatFlow(double flow) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (flow < 1024) {
            fileSizeString = df.format(flow) + "B";
        } else if (flow < 1048576) {
            fileSizeString = df.format(flow / 1024) + "K";
        } else if (flow < 1073741824) {
            fileSizeString = df.format(flow / 1048576) + "M";
        } else {
            fileSizeString = df.format(flow / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String calcSign(Map<String, String> params, String secret) {
        params = new TreeMap<>(params);
        StringBuilder stringBuilder = new StringBuilder();
        int size = params.size();
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            index++;
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            if (index < size) {
                stringBuilder.append("&");
            }
        }
        stringBuilder.append(secret);
        return DigestUtils.sha256Hex(stringBuilder.toString());
    }

    public static String post(String uri, Map<String, String> params) throws Exception {
        HttpPost httpPost = new HttpPost(uri);
        RequestConfig requestConfig = RequestConfig.DEFAULT;
        httpPost.setConfig(requestConfig);
        List paramList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(paramList, "utf-8");
        httpPost.setEntity(paramEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpPost);
        HttpEntity resultEntity = response.getEntity();
        String result = EntityUtils.toString(resultEntity);
        EntityUtils.consume(resultEntity);
        response.close();
        httpclient.close();
        return result;
    }

    public static String get(String uri, Map<String, String> params) throws Exception {
        String queryString = "?";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryString += entry.getKey() + "=" + entry.getValue() + "&";
        }
        queryString = queryString.substring(0, queryString.length() - 1);

        HttpGet httpGet = new HttpGet(uri + queryString);
        RequestConfig requestConfig = RequestConfig.DEFAULT;
        httpGet.setConfig(requestConfig);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity resultEntity = response.getEntity();
        String result = EntityUtils.toString(resultEntity);
        EntityUtils.consume(resultEntity);
        response.close();
        httpclient.close();
        return result;
    }

    //89860117750007894331
    public static Map<String, String> simDetail(String iccid) throws Exception {

        String secret = resourceBundle.getString("simboss.secret");
        String appid = resourceBundle.getString("simboss.appid");
        String apiUrl = resourceBundle.getString("simboss.url.device.detail");

        //参数
        Map<String, String> params = new TreeMap<>();
        //系统参数
        params.put("appid", appid);
        params.put("timestamp", System.currentTimeMillis() + "");

        //应用参数
        params.put("iccid", iccid);

        //签名
        String sign = calcSign(params, secret);
        params.put("sign", sign);

        //提交请求
        String json = post(apiUrl, params);

        JSONObject resObj = JSONObject.parseObject(json);

        if (resObj.getBoolean("success")) {
            Map<String, String> resMap = new HashMap<>();
            double total = resObj.getJSONObject("data").getDoubleValue("totalDataVolume");
            double usage = resObj.getJSONObject("data").getDoubleValue("usedDataVolume");

            resMap.put("remainFlow", formatFlow((total - usage)* 1024 * 1024));
            String validateTime = resObj.getJSONObject("data").getString("expireDate");
            validateTime = validateTime.substring(0, 10);
            resMap.put("validateTime", validateTime);
            return resMap;
        } else {
            //if("404".equals(resObj.getString("code"))) {
                //使用另外一个平台试试
                String shingsou_url_carddata = resourceBundle.getString("shingsou.url.carddata");
                Map<String, String> getParams = new TreeMap<>();
                getParams.put("cardNo", iccid);
                json = get(shingsou_url_carddata, getParams);
                resObj = JSONObject.parseObject(json);
                if(resObj.getInteger("code") == 0) {
                    Map<String, String> resMap = new HashMap<>();
                    double total = resObj.getJSONObject("msg").getDoubleValue("Balance");
                    double usage = resObj.getJSONObject("msg").getDoubleValue("Used");

                    resMap.put("remainFlow", formatFlow((total - usage)* 1024 * 1024));
                    //诡异格式 格式实例：2018-01-25T07:48:21.063Z
                    String validateTime = resObj.getJSONObject("msg").getString("ExpireTime");
                    if(validateTime == null) {
                        resMap.put("validateTime", "长期有效");
                    }else{
                        //validateTime = validateTime.replace("Z", " UTC");
                        //resMap.put("validateTime", format_display.format(format_utc.parse(validateTime)));
                        resMap.put("validateTime", validateTime.substring(0, 10));
                    }
                    return resMap;
                }else{
                    logger.error("平台：shingsou 查询4g卡[" + iccid + "]信息失败: " + json);
                    return null;
                }
            /*}else {
                logger.error("平台：simboss 查询4g卡[" + iccid + "]信息失败: " + json);
                return null;
            }*/
        }
    }

    /*public static void main(String[] args) throws Exception {
        System.out.println(simDetail("89860117750007894331"));
        String utc_time = "2018-01-25T07:48:21.063Z";
        utc_time = utc_time.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format2.format(format.parse(utc_time)));
    }*/

}
