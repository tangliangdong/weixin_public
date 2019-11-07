package xiaotang.weixin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 探路者 2015/7/20
 */
public abstract class HttpUtils {
    public final static <T> T post(Class<T> t, Object parameterMap, String url) {
        Assert.hasText(url, "请求地址不能为空");
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000*60).setConnectTimeout(1000*60).build();//设置请求和传输超时时间
        List<NameValuePair> params = getParamsList(parameterMap);
        UrlEncodedFormEntity formEntity = null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        String res = null;
        post = new HttpPost(url);
        post.setConfig(requestConfig);
        try {
            formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(formEntity);
            response = httpClient.execute(post);
            res = EntityUtils.toString(response.getEntity());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            post.abort();
            closeResponse(response);
        }
        return JSON.parseObject(res, t);
    }


    public static String postJson(String url, JSONObject json) {
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            StringEntity s = new StringEntity(json.toString(), "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            post.abort();
            closeResponse(response);
        }
    }

    public static String postString(String url, List<NameValuePair> params) {
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000*60).setConnectTimeout(1000*60).build();//设置请求和传输超时时间
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        System.out.println(url);
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                String resopnse = "";
                if (entity != null) {
                    resopnse = EntityUtils.toString(entity, "utf-8");
                }
                return resopnse;
            } else {
                EntityUtils.toString(entity, "utf-8");
                throw new ClientProtocolException("http请求异常状态码: " + status);
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            post.abort();
            closeResponse(response);
        }
    }


    public final static <T> T doPut(Class<T> t, Map<String, String> param, String url) {
        Assert.hasText(url, "请求地址不能为空");
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000*60).setConnectTimeout(1000*60).build();//设置请求和传输超时时间
        List<NameValuePair> params = new ArrayList<>();
        for (String key : param.keySet()) {
            params.add(new BasicNameValuePair(key, param.get(key)));
        }
        UrlEncodedFormEntity formEntity = null;
        HttpPut post = null;
        CloseableHttpResponse response = null;
        String res = null;
        post = new HttpPut(url);
        post.setConfig(requestConfig);
        try {
            formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(formEntity);
            response = httpClient.execute(post);
            res = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            post.abort();
            closeResponse(response);
        }
        return JSON.parseObject(res, t);
    }

    public final static <T> T get(Class<T> t, Object object, String url) {
        Assert.hasText(url, "请求地址不能为空");
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        String res = null;
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(getParamsList(object), "UTF-8"));
            get.setURI(new URI(get.getURI().toString() + "?" + str));
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            get.abort();
            closeResponse(response);
        }
        return JSON.parseObject(res, t);
    }

    private static void closeResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void closeHttpclient(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取参数数据
     *
     * @param object
     * @return
     */
    private final static List<NameValuePair> getParamsList(Object object) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (object == null) {
            return params;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            if (!"class".equals(name)) {
                Object val = beanWrapper.getPropertyValue(name);
                Field field = FieldUtils.getField(object.getClass(), name, true);
                if (field != null) {
                    JSONField jsonField = field.getAnnotation(JSONField.class);
                    if (jsonField != null) {
                        String jsonFieldName = (String) AnnotationUtils.getValue(jsonField, "name");
                        name = StringUtils.hasText(jsonFieldName) ? jsonFieldName : name;
                    }
                }
                if (val != null) {
                    NameValuePair nameValuePair = new BasicNameValuePair(name, val.toString());
                    params.add(nameValuePair);
                }
            }
        }
        return params;
    }


    public final static JSONObject get(List<NameValuePair> params, String url) {
        Assert.hasText(url, "请求地址不能为空");
        CloseableHttpClient httpClient = HttpUtilPool.getConnection();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, "UTF-8"));
            get.setURI(new URI(get.getURI().toString() + "?" + str));
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                String resopnse = "";
                if (entity != null) {
                    resopnse = EntityUtils.toString(entity, "utf-8");
                }
                return JSON.parseObject(resopnse);
            } else {
                EntityUtils.toString(entity, "utf-8");
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            get.abort();
            closeResponse(response);
        }
    }
}