package xiaotang.weixin.configuration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledJobOneDay {

//    @Scheduled(cron="0 0/1 * * * ?") //每分钟执行一次
//    public void statusCheck() {
//        System.out.println("每分钟执行一次。开始……");
//        System.out.println("每分钟执行一次。结束。");
//    }
//
//    @Scheduled(cron="0 13 16 ? * *") //每分钟执行一次
//    public void statusCheck2() {
//        System.out.println("指定时间执行一次。开始……");
//        System.out.println("指定时间执行一次。结束。");
//    }
//
//    @Scheduled(fixedRate=20000)
//    public void testTasks() {
//        System.out.println("每20秒执行一次。开始……");
//        System.out.println("每20秒执行一次。结束。");
//    }
}