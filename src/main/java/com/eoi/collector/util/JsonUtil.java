package com.eoi.collector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lemon
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }

    private JsonUtil() {
    }

    public static JsonNode decodeJson(String json) throws IOException {
        return objectMapper.readTree(json);
    }

    public static ObjectNode decodeJsonObject(String json) throws IOException {
        return (ObjectNode)objectMapper.readTree(json);
    }

    public static ArrayNode decodeJsonArray(String json) throws IOException {
        return (ArrayNode)objectMapper.readTree(json);
    }

    public static String encode(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static String encodePretty(Object obj) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static JsonNode decode2Json(String value) throws IOException {
        return objectMapper.readTree(value);
    }

    public static String encodeNotThrowException(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("json encode error {}", e);
            return "json encode error";
        }
    }

    /**
     * 将json string反序列化成对象
     *
     * @param json
     * @param valueType
     * @return
     */
    public static <T> T decode(String json, Class<T> valueType) throws IOException {
        return objectMapper.readValue(json, valueType);
    }

    /**
     * 将json array反序列化为对象
     *
     * @param json
     * @param typeReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T decode(String json, TypeReference<T> typeReference) throws IOException {
        return (T) objectMapper.readValue(json, typeReference);
    }

    public static Map<String, String> json2StringMap(String json) throws Exception{
        if (StringUtils.isEmpty(json)) {
            throw new Exception("configJson is empty");
        }

        Map jsonMap = decode(json, Map.class);

        Map<String, String> props = new HashMap<>();

        for (Object key : jsonMap.keySet()) {
            props.put(key.toString(), jsonMap.get(key).toString());
        }

        return props;
    }

    public static Map<String, Object> json2MapNotThrowException(String json) {
        try {
            return (Map<String, Object>)decode(json, Map.class);
        } catch (Exception e) {
            logger.error("json2Map, error", e);
            return null;
        }
    }

    public static Map<String, Object> json2Map(String json) throws IOException {
        return (Map<String, Object>)decode(json, Map.class);
    }
}