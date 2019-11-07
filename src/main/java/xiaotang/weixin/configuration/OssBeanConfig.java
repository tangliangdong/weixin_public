package xiaotang.weixin.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Administrator on 2018/1/3.
 */
@ConfigurationProperties(prefix = "oss")
public class OssBeanConfig {
    private  String bucket;
    private  String endpoint;
    private  String dirRoot;
    private  String backAccessKeyId;
    private  String backAccessKeySecret;
    private  String frontAccessKeyId;
    public  String frontAccessKeySecret;
    public  String frontRoleSessionName;
    public  String frontRegionCnHangzhou;
    public  String frontStsApiVersion;
    public  String stsRoleArn;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDirRoot() {
        return dirRoot;
    }

    public void setDirRoot(String dirRoot) {
        this.dirRoot = dirRoot;
    }

    public String getBackAccessKeyId() {
        return backAccessKeyId;
    }

    public void setBackAccessKeyId(String backAccessKeyId) {
        this.backAccessKeyId = backAccessKeyId;
    }

    public String getBackAccessKeySecret() {
        return backAccessKeySecret;
    }

    public void setBackAccessKeySecret(String backAccessKeySecret) {
        this.backAccessKeySecret = backAccessKeySecret;
    }

    public String getFrontAccessKeyId() {
        return frontAccessKeyId;
    }

    public void setFrontAccessKeyId(String frontAccessKeyId) {
        this.frontAccessKeyId = frontAccessKeyId;
    }

    public String getFrontAccessKeySecret() {
        return frontAccessKeySecret;
    }

    public void setFrontAccessKeySecret(String frontAccessKeySecret) {
        this.frontAccessKeySecret = frontAccessKeySecret;
    }

    public String getFrontRoleSessionName() {
        return frontRoleSessionName;
    }

    public void setFrontRoleSessionName(String frontRoleSessionName) {
        this.frontRoleSessionName = frontRoleSessionName;
    }

    public String getFrontRegionCnHangzhou() {
        return frontRegionCnHangzhou;
    }

    public void setFrontRegionCnHangzhou(String frontRegionCnHangzhou) {
        this.frontRegionCnHangzhou = frontRegionCnHangzhou;
    }

    public String getFrontStsApiVersion() {
        return frontStsApiVersion;
    }

    public void setFrontStsApiVersion(String frontStsApiVersion) {
        this.frontStsApiVersion = frontStsApiVersion;
    }

    public String getStsRoleArn() {
        return stsRoleArn;
    }

    public void setStsRoleArn(String stsRoleArn) {
        this.stsRoleArn = stsRoleArn;
    }
}
