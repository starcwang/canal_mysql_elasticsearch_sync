package com.star.sync.elasticsearch.service;

import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 16:19:00
 */
public interface ElasticsearchService {
    void insertById(String index, String type, String id, Map<String, Object> dataMap);

    void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap);

    void update(String index, String type, String id, Map<String, Object> dataMap);

    void deleteById(String index, String type, String id);
}
