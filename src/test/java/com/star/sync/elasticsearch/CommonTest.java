package com.star.sync.elasticsearch;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-09-01 16:22:00
 */
public class CommonTest {

    @Test
    public void testCommon() throws Exception {
        System.out.println(DateTime.parse("2017-09-01 16:21:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate());
        long i = 7989501;
        long maxPK = 9025632;
        System.out.println(String.format("当前同步pk=%s，总共total=%s，进度=%s%%", i, maxPK, new BigDecimal(i).divide(new BigDecimal(maxPK * 100), 2, BigDecimal.ROUND_HALF_DOWN)));
    }
}
