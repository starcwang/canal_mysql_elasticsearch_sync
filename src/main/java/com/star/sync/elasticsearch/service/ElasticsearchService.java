package com.star.sync.elasticsearch.service;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 16:19:00
 */
public interface ElasticsearchService {
    void insertById(String index, String type, String id, String json);

    void update(String index, String type, String id, String json);

    void deleteById(String index, String type, String id);
}
