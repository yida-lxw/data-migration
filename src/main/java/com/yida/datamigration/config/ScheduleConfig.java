package com.yida.datamigration.config;

import com.yida.datamigration.core.ThreadPoolRejectPolicy;
import com.yida.datamigration.core.ThreadPoolRejectPolicyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 定时任务配置
 */
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "lucene.schedule-task.thread-pool")
public class ScheduleConfig {
    /**线程池的核心线程数*/
    @Value("${core-pool-size}")
    private int corePoolSize;

    /**线程池的最大线程数*/
    @Value("${max-pool-size}")
    private int maxPoolSize;

    /**线程池的队列容量*/
    @Value("${queue-capacity}")
    private int queueCapacity;

    /**线程池中空闲线程保活时间,单位:秒*/
    @Value("${keep-alive-seconds}")
    private int keepAliveSeconds;

    /**线程池名称前缀*/
    @Value("${pool-prefix}")
    private String threadPoolPrefix;

    /**当执行关闭线程池操作时,是否需要等待线程池中所有线程任务执行完毕再关闭线程池, true=等待,false=不等待*/
    @Value("${wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForJobsToCompleteOnShutdown;

    /**线程池拒绝策略的字符串key，具体见ThreadPoolRejectPolicy枚举类*/
    @Value("${reject-policy}")
    private String threadPoolRejectPolicyKey;


    @Bean("luceneScheduleTaskThreadPoolExecutor")
    public TaskExecutor luceneScheduleTaskThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
        executor.setThreadNamePrefix(threadPoolPrefix);
        ThreadPoolRejectPolicy threadPoolRejectPolicy = ThreadPoolRejectPolicy.of(threadPoolRejectPolicyKey);
        RejectedExecutionHandler rejectedExecutionHandler = ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(threadPoolRejectPolicy);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.initialize();
        return executor;
    }

}
