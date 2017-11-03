package com.star.sync.elasticsearch;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-09-01 16:22:00
 */
public class CommonTest {

    @Test
    public void testCommon() throws Exception {
        System.out.println(DateTime.parse("2017-09-01 16:21:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate());
//        long i = 7989501;
        long maxPK = 9025632;

        for (long j = 0; j < 9025632; j++) {
            System.out.println(String.format("当前同步pk=%s，总共total=%s，进度=%s%%", j, maxPK, new BigDecimal(j * 100).divide(new BigDecimal(maxPK), 2, BigDecimal.ROUND_HALF_UP)));
        }
    }

    @Test
    public void testThread() throws Exception {
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("11");
                return t;
            }
        });
        for (int i = 0; i < 5; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + " is run");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        Thread.sleep(10000);
        executorService.shutdown();
    }
}
