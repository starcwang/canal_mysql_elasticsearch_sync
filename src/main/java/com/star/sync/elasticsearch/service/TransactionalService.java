package com.star.sync.elasticsearch.service;

import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.model.request.SyncByTableRequest;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-05-21 23:22:00
 */
public interface TransactionalService {

    /**
     * 开启事务的读取mysql并插入到Elasticsearch中（读锁）
     */
    void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel);
}
