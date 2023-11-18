package com.ws.base.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import com.ws.enu.DefaultInfo;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author GSF
 * <p>响应数据格式</p>
 */
@Data
public class ResultBody<T> implements Serializable {

    /**
     * <p>响应结果</p>
     *
     * @author wangshuhunyin
     **/
    T data;

    /**
     * <p>响应代码</p>
     *
     * @author wangshuhunyin
     **/
    String code;

    /**
     * <p>响应消息</p>
     *
     * @author wangshuhunyin
     **/
    String message;

    boolean status;

    public ResultBody() {
    }

    /**
     * @param data    返回数据
     * @param message 提示消息
     * @return ResultBody
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> build(T data, String code, String message, boolean status) {
        ResultBody<T> resultBody = new ResultBody<>();
        resultBody.setData(data);
        resultBody.setCode(code);
        resultBody.setMessage(message);
        resultBody.setStatus(status);
        return resultBody;
    }

    /**
     * @return ResultBody
     * <p>成功</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> success() {
        return ResultBody.success(null);
    }

    /**
     * @return ResultBody
     * <p>成功</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> success(T data) {
        return ResultBody.success(data, DefaultInfo.SUCCESS.getResultMsg());
    }

    /**
     * @param data 返回数据
     * @return ResultBody
     * <p>成功</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> success(T data, String message) {
        return ResultBody.success(data, DefaultInfo.SUCCESS.getResultCode(), message);
    }

    /**
     * @param data 返回数据
     * @return ResultBody
     * <p>成功</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> success(T data, String code, String message) {
        return ResultBody.build(data, code, message, true);
    }

    /**
     * @return ResultBody
     * <p>警告</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> warn() {
        return ResultBody.warn(null);
    }

    /**
     * @return ResultBody
     * <p>警告</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> warn(String message) {
        return ResultBody.warn(null, message);
    }

    /**
     * @return ResultBody
     * <p>警告</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> warn(T data) {
        return ResultBody.warn(data, DefaultInfo.SUCCESS.getResultMsg());
    }

    /**
     * @param data 返回数据
     * @return ResultBody
     * <p>警告</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> warn(T data, String message) {
        return ResultBody.warn(data, "-1", message);
    }

    /**
     * @param data 返回数据
     * @return ResultBody
     * <p>警告</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> warn(T data, String code, String message) {
        return ResultBody.build(data, code, message, false);
    }

    /**
     * @param message 消息
     * @return ResultBody
     * <p>失败</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> error(String message) {
        return ResultBody.error(DefaultInfo.ERROR.getResultCode(), message);
    }

    public static <T> ResultBody<T> error(DefaultInfo defaultInfo) {
        return ResultBody.error(defaultInfo.getResultCode(), defaultInfo.getResultMsg());
    }

    /**
     * @param code    返回数据
     * @param message 消息
     * @return ResultBody
     * <p>失败</p>
     * @author wangshuhunyin
     **/
    public static <T> ResultBody<T> error(String code, String message) {
        return ResultBody.build(null, code, message, false);
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

    public Map<String, Object> toMap() {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = this.getClass();
        while (clazz != null) {
            fields.addAll(List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
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

}
