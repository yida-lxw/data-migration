package com.yida.datamigration.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Lucene定时任务类
 */
@Component
public class LuceneSchedulerTask {
    private static final Logger log = LoggerFactory.getLogger(LuceneSchedulerTask.class);


    @Async("luceneScheduleTaskThreadPoolExecutor")
    //@Scheduled(cron="*/5 * * * * ?")
    //@Scheduled(cron="0 0 */2 * * ?")
    public void luceneScheduleTask(){
        log.info("定时任务每间隔5秒执行一次");
    }
}
