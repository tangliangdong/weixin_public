package xiaotang.weixin.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by songxw on 2016-12-29.
 */
@Component
public class HttpUtilPool {

    static Logger logger = LoggerFactory.getLogger(HttpUtilPool.class);

    public static PoolingHttpClientConnectionManager poolConnManager;
    /**
     * 最大连接数
     */
    private static int max_total_connections =70;
    /**
     * 获取连接的最大等待时间
     */
    private static int wait_timeout=5000;
    /**
     * 每个路由最大连接数
     */
    private static int max_route_connections=50;
    /**
     * 连接超时时间
     */
    private static int connect_timeout=5000;
    /**
     * 读取超时时间
     */
    private static int read_timeout=10000;

    public PoolingHttpClientConnectionManager getPoolConnManager() {
        return poolConnManager;
    }

    public void setPoolConnManager(PoolingHttpClientConnectionManager poolConnManager) {
        poolConnManager = poolConnManager;
    }

    public int getMax_total_connections() {
        return max_total_connections;
    }

    public void setMax_total_connections(int max_total_connections) {
        this.max_total_connections = max_total_connections;
    }

    public int getWait_timeout() {
        return wait_timeout;
    }

    public void setWait_timeout(int wait_timeout) {
        this.wait_timeout = wait_timeout;
    }

    public int getMax_route_connections() {
        return max_route_connections;
    }

    public void setMax_route_connections(int max_route_connections) {
        this.max_route_connections = max_route_connections;
    }

    public int getConnect_timeout() {
        return connect_timeout;
    }

    public void setConnect_timeout(int connect_timeout) {
        this.connect_timeout = connect_timeout;
    }

    public int getRead_timeout() {
        return read_timeout;
    }

    public void setRead_timeout(int read_timeout) {
        this.read_timeout = read_timeout;
    }

    @PostConstruct
    public void init() {
        logger.info("初始化http连接池...");
        //            //需要通过以下代码声明对https连接支持
//            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,
//                    new TrustSelfSignedStrategy())
//                    .build();
//            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,hostnameVerifier);
//            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                    .register("https", sslsf)
//                    .build();
//            //初始化连接管理器
//            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // Increase max total connection to 200
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(max_total_connections);
        // Increase default max connection per route to 20
        poolConnManager.setDefaultMaxPerRoute(max_route_connections);
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(wait_timeout).build();
        poolConnManager.setDefaultSocketConfig(socketConfig);
        logger.info("初始化http连接池成功！");

    }

    /**
     * 从连接池中获取客户端
     *
     * @return
     */
    public static CloseableHttpClient getConnection() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(read_timeout)
                .setConnectTimeout(connect_timeout).setSocketTimeout(wait_timeout).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig).build();
        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {
            logger.info("当前http连接池状态:" + poolConnManager.getTotalStats().toString());
        }
        return httpClient;
    }
}
