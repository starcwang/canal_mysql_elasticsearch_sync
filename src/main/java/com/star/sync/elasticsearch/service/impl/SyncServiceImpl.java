package com.star.sync.elasticsearch.service.impl;

import com.star.sync.elasticsearch.dao.BaseDao;
import com.star.sync.elasticsearch.model.DatabaseTableModel;
import com.star.sync.elasticsearch.model.IndexTypeModel;
import com.star.sync.elasticsearch.model.request.SyncByTableRequest;
import com.star.sync.elasticsearch.service.MappingService;
import com.star.sync.elasticsearch.service.SyncService;
import com.star.sync.elasticsearch.service.TransactionalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private MappingService mappingService;

    @Resource
    private TransactionalService transactionalService;

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
                    transactionalService.batchInsertElasticsearch(request, primaryKey, i, i + request.getStepSize(), indexTypeModel);
                    logger.info(String.format("当前同步pk=%s，总共total=%s，进度=%s%%", i, maxPK, new BigDecimal(i * 100).divide(new BigDecimal(maxPK), 3, BigDecimal.ROUND_HALF_UP)));
                }
            } catch (Exception e) {
                logger.error("批量转换并插入Elasticsearch异常", e);
            }
        });
        return true;
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
