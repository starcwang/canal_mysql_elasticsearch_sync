package com.star.sync.elasticsearch.service.impl;

import com.star.sync.elasticsearch.dao.BaseDao;
import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.model.request.SyncByTableRequest;
import com.star.sync.elasticsearch.service.ElasticsearchService;
import com.star.sync.elasticsearch.service.MappingService;
import com.star.sync.elasticsearch.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-31 17:48:00
 */
@Service
public class SyncServiceImpl implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);

    @Resource
    private BaseDao baseDao;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private MappingService mappingService;

    @Override
    public boolean syncByTable(SyncByTableRequest request) {
        IndexTypeModel indexTypeModel = mappingService.getIndexType(new DatabaseTableModel(request.getDatabase(), request.getTable()));
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(request.getDatabase() + "." + request.getTable())).orElse("id");
        if (indexTypeModel == null) {
            throw new IllegalArgumentException(String.format("配置文件中缺失database=%s和table=%s所对应的index和type的映射配置", request.getDatabase(), request.getTable()));
        }
        new Thread(() -> {
            try {
                long maxPK = baseDao.selectMaxPK(primaryKey, request.getDatabase(), request.getTable());
                for (long i = 1; i < maxPK; i += request.getStepSize()) {
                    batchInsertElasticsearch(request, primaryKey, i, i + request.getStepSize(), indexTypeModel);
                    logger.info("当前同步pk=%s，总共total=%s，进度=%s", i, maxPK, (double) (i / maxPK * 100) + "%");
                }
            } catch (Exception e) {
                logger.error("批量转换并插入Elasticsearch异常", e);
            }
        }).start();
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel) {
        List<Map<String, Object>> dataList = baseDao.selectByPKIntervalLockInShareMode(primaryKey, from, to, request.getDatabase(), request.getTable());
        dataList = convertDateType(dataList);
        Map<String, Map<String, Object>> dataMap = dataList.parallelStream().collect(Collectors.toMap(strObjMap -> String.valueOf(strObjMap.get(primaryKey)), map -> map));
        elasticsearchService.batchInsertById(indexTypeModel.getIndex(), indexTypeModel.getType(), dataMap);
    }

    private List<Map<String, Object>> convertDateType(List<Map<String, Object>> source) {
        source.parallelStream().forEach(map -> map.forEach((key, value) -> {
            if (value instanceof Timestamp) {
                map.put(key, LocalDateTime.ofInstant(((Timestamp) value).toInstant(), ZoneId.systemDefault()));
            }
        }));
        return source;
    }
}
