package com.ws.utils;

import com.ws.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class JavaTypeMapInfo {

    private static final Map<String, String> JAVA_SIMPLE_NAME_MAP_NAME = new HashMap<>();

    static {
        JAVA_SIMPLE_NAME_MAP_NAME.put("String", "java.lang.String");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Long", "java.lang.Long");
        JAVA_SIMPLE_NAME_MAP_NAME.put("long", "java.lang.Long");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Integer", "java.lang.Integer");
        JAVA_SIMPLE_NAME_MAP_NAME.put("int", "java.lang.Integer");
        JAVA_SIMPLE_NAME_MAP_NAME.put("BigDecimal", "java.lang.BigDecimal");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Short", "java.lang.Short");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Float", "java.lang.Float");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Double", "java.lang.Double");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Char", "java.lang.Character");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Boolean", "java.lang.Boolean");
        JAVA_SIMPLE_NAME_MAP_NAME.put("boolean", "java.lang.Boolean");
        JAVA_SIMPLE_NAME_MAP_NAME.put("Date", "java.util.Date");
    }

    public static String getNameFromSimpleName(String simpleName) throws IllegalArgumentException {
        String name = JAVA_SIMPLE_NAME_MAP_NAME.get(simpleName);
        if (StringUtil.isEmpty(name)) {
            throw new IllegalArgumentException("Unsupported SimpleName: " + simpleName);
        }
        return name;
    }

}
