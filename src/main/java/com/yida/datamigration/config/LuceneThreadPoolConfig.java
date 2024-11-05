package com.yida.datamigration.config;

import com.yida.datamigration.core.BoundedBlockingTransferQueue;
import com.yida.datamigration.core.CustomThreadPoolExecutor;
import com.yida.datamigration.core.NamedThreadFactory;
import com.yida.datamigration.core.ThreadPoolRejectPolicy;
import com.yida.datamigration.core.ThreadPoolRejectPolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Lucene线程池配置类
 */
@EnableAsync
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "lucene.index-read.thread-pool")
public class LuceneThreadPoolConfig {
    private static final Logger log = LoggerFactory.getLogger(LuceneThreadPoolConfig.class);

    /***********************************************线程池相关配置*******************************************************/
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

    /**
     * 用于Lucene读取索引数据时多线程并行读取段文件
     * @return
     */
    @Bean("luceneIndexReadThreadPoolExecutor")
    public ExecutorService luceneIndexReadThreadPoolExecutor() {
        ThreadFactory threadFactory = new NamedThreadFactory(threadPoolPrefix);
        ThreadPoolRejectPolicy threadPoolRejectPolicy = ThreadPoolRejectPolicy.of(threadPoolRejectPolicyKey);
        RejectedExecutionHandler rejectedExecutionHandler = ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(threadPoolRejectPolicy);
        return new CustomThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS,
                new BoundedBlockingTransferQueue<Runnable>(queueCapacity), threadFactory, rejectedExecutionHandler);
    }
}
