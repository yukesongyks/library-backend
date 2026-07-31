package com.library.common.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花ID生成器(简化版)
 * <p>
 * 用于生成全局唯一的主键ID. 生产环境建议替换为完整的雪花算法实现.
 *
 * @author library-team
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private static final long START_TIMESTAMP = 1288834974657L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    private static volatile long LAST_TIMESTAMP = -1L;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return nextId();
    }

    /**
     * 生成下一个ID
     */
    private static synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < LAST_TIMESTAMP) {
            throw new RuntimeException("时钟回拨, 拒绝生成ID");
        }

        if (currentTimestamp == LAST_TIMESTAMP) {
            long sequence = SEQUENCE.incrementAndGet() & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = tilNextMillis(LAST_TIMESTAMP);
            }
        } else {
            SEQUENCE.set(0);
        }

        LAST_TIMESTAMP = currentTimestamp;
        return (currentTimestamp - START_TIMESTAMP) << SEQUENCE_BITS | (SEQUENCE.get() & MAX_SEQUENCE);
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
