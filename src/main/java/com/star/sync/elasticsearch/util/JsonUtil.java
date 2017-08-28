package com.star.sync.elasticsearch.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * json格式转换通用类
 *
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:54:00
 */
public abstract class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 对象映射
     */
    private static final ObjectMapper objMapper = new ObjectMapper();

    static {
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
        objMapper.setSerializationInclusion(Include.NON_NULL);
    }


    /**
     * Java对象转换为Json串
     *
     * @param obj Java对象
     * @return Json串
     */
    public static String toJson(Object obj) {
        String rst;
        if (obj == null || obj instanceof String) {
            return (String) obj;
        }
        try {
            rst = objMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("将Java对象转换成Json串出错！");
            throw new RuntimeException("将Java对象转换成Json串出错！", e);
        }
        return rst;
    }

    /**
     * Json串转换为Java对象
     *
     * @param json Json串
     * @param type Java对象类型
     * @return Java对象
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T rst;
        try {
            rst = objMapper.readValue(json, type);
        } catch (Exception e) {
            logger.error("Json串转换成对象出错：{}", json);
            throw new RuntimeException("Json串转换成对象出错!", e);
        }
        return rst;
    }

    /**
     * Json串转换为Java对象
     * <br>使用引用类型，适用于List&ltObject&gt、Set&ltObject&gt 这种无法直接获取class对象的场景
     * <br>使用方法：TypeReference ref = new TypeReference&ltList&ltInteger&gt&gt(){};
     *
     * @param json    Json串
     * @param typeRef Java对象类型引用
     * @return Java对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        T rst;
        try {
            rst = objMapper.readValue(json, typeRef);
        } catch (Exception e) {
            logger.error("Json串转换成对象出错：{}", json);
            throw new RuntimeException("Json串转换成对象出错!", e);
        }
        return rst;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> fromJsonToMap(String json) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            map = objMapper.readValue(json, map.getClass());
        } catch (IOException e) {
            logger.error("Json串转换成对象出错：{}", json);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> toMap(String json) {
        HashMap<String, Object> map;
        try {
            map = objMapper.readValue(json, HashMap.class);
        } catch (Exception e) {
            map = null;
            logger.error("Json串转换成对象出错：{}", json);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static List<HashMap<String, Object>> fromJsonToList(String json) {
        List<HashMap<String, Object>> list;
        try {
            list = objMapper.readValue(json, List.class);
        } catch (IOException e) {
            logger.error("Json串转换成对象出错：{}", json);
            throw new RuntimeException("Json串转换成List出错!", e);
        }
        return list;
    }
}
