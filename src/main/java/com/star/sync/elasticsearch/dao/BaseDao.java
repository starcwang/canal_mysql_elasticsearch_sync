package com.star.sync.elasticsearch.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-29 10:17:00
 */
@Repository
public interface BaseDao {

    Map<String, Object> selectByPK(@Param("key") String key, @Param("value") Object value, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKs(@Param("key") String key, @Param("valueList") List<Object> valueList, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKsLockInShareMode(@Param("key") String key, @Param("valueList") List<Object> valueList, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long count(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long selectMaxPK(@Param("key") String key, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long selectMinPK(@Param("key") String key, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKInterval(@Param("key") String key, @Param("minPK") long minPK, @Param("maxPK") long maxPK, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKIntervalLockInShareMode(@Param("key") String key, @Param("minPK") long minPK, @Param("maxPK") long maxPK, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

}
