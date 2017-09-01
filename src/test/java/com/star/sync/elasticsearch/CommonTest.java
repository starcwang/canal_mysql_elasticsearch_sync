package com.star.sync.elasticsearch;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-09-01 16:22:00
 */
public class CommonTest {

    @Test
    public void testCommon() throws Exception {
        System.out.println(DateTime.parse("2017-09-01 16:21:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate());
    }
}
