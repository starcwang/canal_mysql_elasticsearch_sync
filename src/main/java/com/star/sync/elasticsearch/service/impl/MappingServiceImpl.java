package com.star.sync.elasticsearch.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.service.MappingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-27 13:14:00
 */
@Service
@PropertySource("classpath:mapping.properties")
@ConfigurationProperties
public class MappingServiceImpl implements MappingService, InitializingBean {

    //    @Value("${dbEsMapping}")
    private Map<String, String> dbEsMapping;
    private BiMap<DatabaseTableModel, IndexTypeModel> dbEsBiMapping;

    @Override
    public IndexTypeModel getIndexType(DatabaseTableModel databaseTableModel) {
        return dbEsBiMapping.get(databaseTableModel);
    }

    @Override
    public DatabaseTableModel getDatabaseTableModel(IndexTypeModel indexTypeModel) {
        return dbEsBiMapping.inverse().get(indexTypeModel);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dbEsBiMapping = HashBiMap.create();
        dbEsMapping.forEach((key, value) -> {
            String[] keyStrings = StringUtils.split(key, ".");
            String[] valueStrings = StringUtils.split(value, ".");
            dbEsBiMapping.put(new DatabaseTableModel(keyStrings[0], keyStrings[1]), new IndexTypeModel(valueStrings[0], valueStrings[1]));
        });
    }

    public Map<String, String> getDbEsMapping() {
        return dbEsMapping;
    }

    public void setDbEsMapping(Map<String, String> dbEsMapping) {
        this.dbEsMapping = dbEsMapping;
    }
}
