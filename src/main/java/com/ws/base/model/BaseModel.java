package com.ws.base.model;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import com.ws.utils.RequestUtil;
import com.ws.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GSF
 * <p>BaseModel</p>
 */
public class BaseModel implements Serializable {

    public List<Field> obtainFields() {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = this.getClass();
        Map<String, String> temp = new HashMap<>();
        while (clazz != null) {
            List.of(clazz.getDeclaredFields()).forEach(item -> {
                String name = item.getName();
                if (Objects.isNull(temp.get(name))) {
                    fields.add(item);
                    temp.put(name, name);
                }
            });
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public Map<String, Field> obtainMapFields() {
        List<Field> fields = this.obtainFields();
        return fields.stream().collect(Collectors.toMap(Field::getName, value -> value));
    }

    public void setAnyValue(String name, Object value) {
        this.setValuesByMap(new HashMap<>() {{
            put(name, value);
        }});
    }

    public Object obtainAnyValue(String name) {
        return this.toMap().get(name);
    }

    public void setValuesByMap(Map<String, Object> map) {
        if (Objects.nonNull(map)) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            Map<String, Field> fields = this.obtainMapFields();
            fields.forEach((k, v) -> {
                if (Objects.nonNull(map.get(k))) {
                    v.setAccessible(true);
                    try {
                        v.set(this, Convert.convert(v.getType(), map.get(k)));
                    } catch (IllegalAccessException e) {
                        logger.warn(StringUtil.concat("该属性: {} 不存在", k));
                    }
                }
            });
        }
    }

    public void setValuesByRequest(HttpServletRequest request) throws IOException {
        this.setValuesByMap(this.obtainRequestParam(request));
    }

    public void setValuesByModelJson(String json) {
        this.setValuesByMap(JSON.parseObject(json));
    }

    public boolean fieldIsExist(String name) {
        return this.obtainMapFields().containsKey(name);
    }

    public boolean fieldIsExist(Field field) {
        return this.obtainFields().contains(field);
    }

    /**
     * <p>兴许有重写需求</p>
     *
     * @param request 请求
     * @return Map<String, Object> 请求参数
     **/
    public Map<String, Object> obtainRequestParam(HttpServletRequest request) throws IOException {
        return RequestUtil.getRequestParams(request);
    }

    public Map<String, Object> toMap() {
        List<Field> fields = this.obtainFields();
        Map<String, Object> map = new HashMap<>(fields.size());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String toJson(SerializerFeature... features) {
        return JSON.toJSONString(this, features);
    }

    public String toJson(ValueFilter filter, SerializerFeature... features) {
        return JSON.toJSONString(this, filter, features);
    }

    public String toJson(SerializeConfig serializeConfig, SerializerFeature... features) {
        return JSON.toJSONString(this, serializeConfig, features);
    }

    public String toJson(SerializeConfig serializeConfig, SerializeFilter filter, SerializerFeature... features) {
        return this.toJson(serializeConfig, new SerializeFilter[]{filter}, features);
    }

    public String toJson(SerializeConfig serializeConfig, SerializeFilter[] filter, SerializerFeature... features) {
        return JSON.toJSONString(this, serializeConfig, filter, features);
    }

    public String toJsonDateFormat(String format) {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer(format));
        return this.toJson(serializeConfig);
    }

    public String toJsonDateFormat(String format, SerializerFeature... features) {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer(format));
        return this.toJson(serializeConfig, features);
    }

    public String toJsonDateFormat(String format, ValueFilter filter, SerializerFeature... features) {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer(format));
        return this.toJson(serializeConfig, filter, features);
    }

    public String toJsonyMdHms() {
        return this.toJsonDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public String toJsonyMd() {
        return this.toJsonDateFormat("yyyy-MM-dd");
    }

}
