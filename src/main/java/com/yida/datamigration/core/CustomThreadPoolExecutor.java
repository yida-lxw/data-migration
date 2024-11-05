package com.yida.datamigration.core;

import java.util.concurrent.*;

/**
 * 自定义线程池
 * 1. 当核心线程数未满时，直接创建新线程处理任务
 * 2. 当核心线程数已满但最大线程数未满时，继续创建新线程处理任务
 * 3. 当核心线程数和最大线程数均已满，但队列尚未满时，则将任务放入队列
 * 4. 当队列也已满时，则采取拒绝策略进行处理
 * 5. 当任务不繁忙时，线程池会自动将线程池个数维持到核心线程数，多余的线程会退出
 * 6. 新增了一种BlockingPolicy拒绝策略，该拒绝策略的处理逻辑为：
 *    A. 当队列已满时，对任务入队操作进行阻塞，直至队列容量未满为止
 *    B. 其他线程在执行poll/take等操作时，会唤醒当前阻塞于队列已满的线程
 *    C. 任务入队操作阻塞的最大超时时间默认为10秒，10秒后会再次尝试执行任务入队操作
 * 7. 采取BlockingPolicy拒绝策略的好处是能够保证所有任务最终都能被处理，不会自动丢弃任务。
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory("default_threadpool_"),
                new BlockingPolicy());
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                    RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, 0L, TimeUnit.SECONDS, workQueue, threadFactory, rejectedExecutionHandler);
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new BlockingPolicy());
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                    RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
    }

    @Override
    public void execute(Runnable command) {
        int corePoolSize = getCorePoolSize();
        int currentPoolSize = getPoolSize();
        // 当核心线程数未满时,首先尝试创建新线程执行任务
        if (currentPoolSize < corePoolSize) {
            executeTask(command);
        } else {
            int maxPoolSize = getMaximumPoolSize();
            // 当核心线程数已满但最大线程数未满时，创建新线程执行任务
            if(currentPoolSize < maxPoolSize) {
                executeTask(command);
            } else {
                // 当最大线程数已满，则尝试将任务加入队列
                boolean isSuccess = offer(command);
                // 若新任务加入队列失败，则表明队列已满，此时采取拒绝策略进行处理
                if (!isSuccess) {
                    super.getRejectedExecutionHandler().rejectedExecution(command, this);
                }
            }
        }
    }

    /**
     * 往队列中添加任务(供子类重写)
     * @param runnable
     * @return
     */
    protected boolean offer(Runnable runnable) {
        return getQueue().offer(runnable);
    }

    /**
     * 从线程池获取一个线程执行当前任务(供子类重写)
     * @param runnable the task to execute
     */
    protected void executeTask(Runnable runnable) {
        super.execute(runnable);
    }
}
