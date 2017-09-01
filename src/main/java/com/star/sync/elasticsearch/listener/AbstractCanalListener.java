package com.star.sync.elasticsearch.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.star.sync.elasticsearch.event.CanalEvent;
import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.service.MappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-28 14:40:00
 */
public abstract class AbstractCanalListener<EVENT extends CanalEvent> implements ApplicationListener<EVENT> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCanalListener.class);

    @Resource
    private MappingService mappingService;

    @Override
    public void onApplicationEvent(EVENT event) {
        Entry entry = event.getEntry();
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        IndexTypeModel indexTypeModel = mappingService.getIndexType(new DatabaseTableModel(database, table));
        if (indexTypeModel == null) {
            return;
        }
        String index = indexTypeModel.getIndex();
        String type = indexTypeModel.getType();
        RowChange change;
        try {
            change = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            logger.error("canalEntry_parser_error,根据CanalEntry获取RowChange失败！", e);
            return;
        }
        change.getRowDatasList().forEach(rowData -> doSync(database, table, index, type, rowData));
    }

    Map<String, Object> parseColumnsToMap(List<Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getIsNull() ? null : mappingService.getElasticsearchTypeObject(column.getMysqlType(), column.getValue()));
        });
        return jsonMap;
    }

    protected abstract void doSync(String database, String table, String index, String type, RowData rowData);
}
