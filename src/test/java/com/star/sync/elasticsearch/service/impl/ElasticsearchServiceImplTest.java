package com.star.sync.elasticsearch.service.impl;

import com.google.common.collect.Maps;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-09-01 15:01:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchServiceImplTest {

    @Resource
    private TransportClient transportClient;

    @Test
    public void insertById() throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", 111);
        map.put("name", "name");
        map.put("date", LocalDateTime.now());
        transportClient.prepareIndex("test", "test", "888").setSource(map).get();
    }

}