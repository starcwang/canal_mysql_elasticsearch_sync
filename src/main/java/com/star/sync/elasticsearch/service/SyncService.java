package com.star.sync.elasticsearch.service;

import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.model.request.SyncByTableRequest;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-31 17:48:00
 */
public interface SyncService {
    boolean syncByTable(SyncByTableRequest request);

    void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel);
}
