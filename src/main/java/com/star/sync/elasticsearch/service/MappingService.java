package com.star.sync.elasticsearch.service;

import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;

import java.util.Map;

/**
 * 配置文件映射服务
 *
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:54:00
 */
public interface MappingService {

    /**
     * 通过database和table，获取与之对应的index和type
     *
     * @param databaseTableModel mysql
     * @return Elasticsearch
     */
    IndexTypeModel getIndexType(DatabaseTableModel databaseTableModel);

    /**
     * 通过index和type，获取与之对应的database和table
     *
     * @param indexTypeModel Elasticsearch
     * @return mysql
     */
    DatabaseTableModel getDatabaseTableModel(IndexTypeModel indexTypeModel);

    /**
     * 获取数据库表的主键映射
     */
    Map<String, String> getTablePrimaryKeyMap();

    /**
     * 设置数据库表的主键映射
     */
    void setTablePrimaryKeyMap(Map<String, String> tablePrimaryKeyMap);

    /**
     * 获取Elasticsearch的数据转换后类型
     *
     * @param mysqlType mysql数据类型
     * @param data      具体数据
     * @return Elasticsearch对应的数据类型
     */
    Object getElasticsearchTypeObject(String mysqlType, String data);
}
