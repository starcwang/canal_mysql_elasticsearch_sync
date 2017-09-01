package com.star.sync.elasticsearch.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.service.MappingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-27 13:14:00
 */
@Service
@PropertySource("classpath:mapping.properties")
@ConfigurationProperties
public class MappingServiceImpl implements MappingService, InitializingBean {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Map<String, String> dbEsMapping;
    private BiMap<DatabaseTableModel, IndexTypeModel> dbEsBiMapping;
    private Map<String, String> tablePrimaryKeyMap;
    private Map<String, Converter> mysqlTypeElasticsearchTypeMapping;

    @Override
    public Map<String, String> getTablePrimaryKeyMap() {
        return tablePrimaryKeyMap;
    }

    @Override
    public void setTablePrimaryKeyMap(Map<String, String> tablePrimaryKeyMap) {
        this.tablePrimaryKeyMap = tablePrimaryKeyMap;
    }

    @Override
    public IndexTypeModel getIndexType(DatabaseTableModel databaseTableModel) {
        return dbEsBiMapping.get(databaseTableModel);
    }

    @Override
    public DatabaseTableModel getDatabaseTableModel(IndexTypeModel indexTypeModel) {
        return dbEsBiMapping.inverse().get(indexTypeModel);
    }

    @Override
    public Object getElasticsearchTypeObject(String mysqlType, String data) {
        Optional<Entry<String, Converter>> result = mysqlTypeElasticsearchTypeMapping.entrySet().parallelStream().filter(entry -> mysqlType.toLowerCase().contains(entry.getKey())).findFirst();
        return (result.isPresent() ? result.get().getValue() : (Converter) data1 -> data1).convert(data);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dbEsBiMapping = HashBiMap.create();
        dbEsMapping.forEach((key, value) -> {
            String[] keyStrings = StringUtils.split(key, ".");
            String[] valueStrings = StringUtils.split(value, ".");
            dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1]));
        });

        mysqlTypeElasticsearchTypeMapping = Maps.newHashMap();
        mysqlTypeElasticsearchTypeMapping.put("char", data -> data);
        mysqlTypeElasticsearchTypeMapping.put("text", data -> data);
        mysqlTypeElasticsearchTypeMapping.put("blob", data -> data);
        mysqlTypeElasticsearchTypeMapping.put("int", Long::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("date", data -> LocalDateTime.parse(data, formatter));
        mysqlTypeElasticsearchTypeMapping.put("time", data -> LocalDateTime.parse(data, formatter));
        mysqlTypeElasticsearchTypeMapping.put("float", Double::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("double", Double::valueOf);
        mysqlTypeElasticsearchTypeMapping.put("decimal", Double::valueOf);
    }

    public Map<String, String> getDbEsMapping() {
        return dbEsMapping;
    }

    public void setDbEsMapping(Map<String, String> dbEsMapping) {
        this.dbEsMapping = dbEsMapping;
    }

    private interface Converter {
        Object convert(String data);
    }
}
