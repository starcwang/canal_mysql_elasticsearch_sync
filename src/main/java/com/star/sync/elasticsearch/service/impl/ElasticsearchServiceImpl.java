package com.star.sync.elasticsearch.service.impl;

import com.star.sync.elasticsearch.service.ElasticsearchService;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-27 12:38:00
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Resource
    private TransportClient transportClient;

    @Override
    public void insertById(String index, String type, String id, String json) {
        transportClient.prepareIndex(index, type, id).setSource(json).get();
    }

    @Override
    public void update(String index, String type, String id, String json) {
        this.insertById(index, type, id, json);
    }

    @Override
    public void deleteById(String index, String type, String id) {
        transportClient.prepareDelete(index, type, id).get();
    }
}
