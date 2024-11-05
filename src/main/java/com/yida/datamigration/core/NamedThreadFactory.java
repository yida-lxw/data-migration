package com.yida.datamigration.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可设置线程池名称的线程池工厂
 */
public class NamedThreadFactory implements ThreadFactory {
    protected static final AtomicInteger POOL_SEQ = new AtomicInteger(1);
    protected final AtomicInteger mThreadNum;
    protected final String threadPoolTag;
    protected final boolean mDaemon;
    protected final ThreadGroup mGroup;

    public NamedThreadFactory() {
        this("threadPool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public NamedThreadFactory(String threadPoolTag) {
        this(threadPoolTag, false);
    }

    public NamedThreadFactory(String threadPoolTag, boolean daemon) {
        if(null == threadPoolTag || "".equals(threadPoolTag)) {
            threadPoolTag = "default";
        }
        this.mThreadNum = new AtomicInteger(1);
        this.threadPoolTag = "threadPool-" + threadPoolTag;
        this.mDaemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.mGroup = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    public Thread newThread(Runnable runnable) {
        String name = this.threadPoolTag + "_" + this.mThreadNum.getAndIncrement();
        Thread ret = new Thread(this.mGroup, runnable, name, 0L);
        ret.setDaemon(this.mDaemon);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return this.mGroup;
    }
}
