package com.star.sync.elasticsearch.service;

import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:54:00
 */
public interface MappingService {

    IndexTypeModel getIndexType(DatabaseTableModel databaseTableModel);

    DatabaseTableModel getDatabaseTableModel(IndexTypeModel indexTypeModel);
}
