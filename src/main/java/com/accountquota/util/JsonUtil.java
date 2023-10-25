package com.accountquota.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    // 注册序列化装置
    static {
        // 设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 私有构造器
     **/
    private JsonUtil() {
    }

    /**
     * 将Object对象转换成Json
     *
     * @param object Object对象
     * @return Json字符串
     */
    public static String convertObject2Json(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("错误object为：{}", object);
            logger.error("Object转换成json错误", e);
        }
        return null;
    }

    /**
     * 将Json转换成Object对象
     *
     * @param json Json字符串
     * @param cls  转换成的对象类型
     * @return 转换之后的对象
     */
    public static <T> T convertJson2Object(String json, Class<T> cls) {
        try {
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            logger.error("转换类型为：{},错误json为：{}", cls, json);
            logger.error("json转换成Object错误", e);
        }
        return null;
    }

    /**
     * 将Json转换成List<Object>对象
     *
     * @param <T>
     * @param json Json字符串
     * @param cls  转换成的对象类型
     * @return 转换之后的对象
     */
    public static <T> List<T> convertJson2ListObject(String json, Class<T> cls) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, cls));
        } catch (Exception e) {
            logger.error("转换类型为：{},错误json为：{}", cls, json);
            logger.error("json转换成List<T>错误", e);
        }
        return null;
    }

    public static Map<String, Object> convertJson2Map(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            logger.error("convertJson2Map json:{}, exception", json, e);
        }
        return null;
    }
}
