package com.yida.datamigration.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;

/**
 * 自定义LinkedTransferQueue
 * 由于LinkedTransferQueue的队列容量没有大小限制，若并发任务个数很多，可能会耗尽JVM内存,
 * 故自定义LinkedTransferQueue，添加最大容量限制，若超过设定的队列最大容量，则对put/offer/add等入队操作进行阻塞
 *
 * @param <E>
 */
public class BoundedBlockingTransferQueue<E> extends LinkedTransferQueue<E> {
    private static final Logger logger = LoggerFactory.getLogger(BoundedBlockingTransferQueue.class);
    /**
     * 队列的默认最大容量: 1024
     */
    public static final int DEFAULT_CAPACITY = 1024;

    private final int capacity;

    private final Semaphore semaphore;

    public BoundedBlockingTransferQueue(int capacity) {
        super();
        if (capacity <= 0) {
            capacity = DEFAULT_CAPACITY;
        }
        this.capacity = capacity;
        this.semaphore = new Semaphore(capacity);
    }

    public BoundedBlockingTransferQueue() {
        this(DEFAULT_CAPACITY);
    }

    @Override
    public boolean offer(E e) {
        try {
            semaphore.acquire();
        } catch (Exception ex) {
            logger.error("Invoking semaphore.acquire() occur exception:[{}]", ex.getMessage());
            return false;
        }
        boolean added = super.offer(e);
        if (!added) {
            semaphore.release();
        }
        return added;
    }

    @Override
    public E take() throws InterruptedException {
        E item = super.take();
        // 当队列中的元素被消费时，应释放许可证
        semaphore.release();
        return item;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        if (removed) {
            semaphore.release();
        }
        return removed;
    }
}