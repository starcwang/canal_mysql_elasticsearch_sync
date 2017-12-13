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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-31 17:48:00
 */
@Service
public class SyncServiceImpl implements SyncService, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);
    /**
     * 使用线程池控制并发数量
     */
    private ExecutorService cachedThreadPool;

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

        long minPK = Optional.ofNullable(request.getFrom()).orElse(baseDao.selectMinPK(primaryKey, request.getDatabase(), request.getTable()));
        long maxPK = Optional.ofNullable(request.getTo()).orElse(baseDao.selectMaxPK(primaryKey, request.getDatabase(), request.getTable()));
        cachedThreadPool.submit(() -> {
            try {
                for (long i = minPK; i < maxPK; i += request.getStepSize()) {
                    batchInsertElasticsearch(request, primaryKey, i, i + request.getStepSize(), indexTypeModel);
                    logger.info(String.format("当前同步pk=%s，总共total=%s，进度=%s%%", i, maxPK, new BigDecimal(i * 100).divide(new BigDecimal(maxPK), 3, BigDecimal.ROUND_HALF_UP)));
                }
            } catch (Exception e) {
                logger.error("批量转换并插入Elasticsearch异常", e);
            }
        });
        return true;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel) {
        List<Map<String, Object>> dataList = baseDao.selectByPKIntervalLockInShareMode(primaryKey, from, to, request.getDatabase(), request.getTable());
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
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

    @Override
    public void afterPropertiesSet() throws Exception {
        cachedThreadPool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (ThreadFactory) Thread::new);
    }

    @Override
    public void destroy() throws Exception {
        if (cachedThreadPool != null) {
            cachedThreadPool.shutdown();
        }
    }
}
