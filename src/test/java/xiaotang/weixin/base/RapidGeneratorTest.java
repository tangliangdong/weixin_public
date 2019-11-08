package xiaotang.weixin.base;

import com.gonghui.pay.code.generator.rapid.RapidGenerator;
import com.gonghui.pay.code.generator.rapid.model.DBConfig;
import org.junit.Test;

/**
 * Created by Administrator on 2015/11/8.
 */

public class RapidGeneratorTest {
    @Test
    public void testGeneratorOneTable() throws Exception {
        RapidGenerator rapidGenerator = new RapidGenerator();
        rapidGenerator.setAuthor("tangliangdong");
        DBConfig dbConfig = new DBConfig();
        dbConfig.setUrl("jdbc:mysql://116.62.14.208:3306/weixin?useUnicode=true&amp;characterEncoding=UTF-8");
        dbConfig.setUserName("root");
        dbConfig.setPwd("123456");
        dbConfig.setDriver("com.mysql.jdbc.Driver");
        rapidGenerator.initDbConfig(dbConfig);
        rapidGenerator.initOutRootPathConfig("E:\\idea\\weixin");
        rapidGenerator.initPackage("xiaotang.weixin");
//        rapidGenerator.initModelName("project");
        rapidGenerator.generatorOneTable("access_token");
    }
}