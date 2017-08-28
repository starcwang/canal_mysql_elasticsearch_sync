package com.star.sync.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-28 17:37:00
 */
@Component
@PropertySource("classpath:mapping.properties")
@ConfigurationProperties
public class MappingConfig {
    private Map<String, String> tablePrimaryKeyMap;

    public Map<String, String> getTablePrimaryKeyMap() {
        return tablePrimaryKeyMap;
    }

    public void setTablePrimaryKeyMap(Map<String, String> tablePrimaryKeyMap) {
        this.tablePrimaryKeyMap = tablePrimaryKeyMap;
    }
}
