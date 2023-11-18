package com.ws.utils;

import javax.lang.model.element.Element;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MysqlTypeMapInfo {

    private static final Map<String, String> JAVA_MAP_MYBATIS_SQL_TYPE = new HashMap<>();
    private static final Map<String, String> SQL_MAP_JAVA_TYPE = new HashMap<>();
    private static final Map<String, Integer> SQL_TYPE_MAP_DEFAULT_LENGTH = new HashMap<>();

    static {
        JAVA_MAP_MYBATIS_SQL_TYPE.put("int", "INTEGER");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("long", "BIGINT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("short", "SMALLINT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("float", "FLOAT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("double", "DOUBLE");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("boolean", "VARCHAR");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("char", "CHAR");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Integer", "INTEGER");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.math.BigDecimal", "DECIMAL");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Long", "BIGINT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Short", "SMALLINT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Float", "FLOAT");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Double", "DOUBLE");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Character", "CHAR");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.Boolean", "VARCHAR");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.lang.String", "VARCHAR");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("[Ljava.lang.Byte", "BLOB");
        JAVA_MAP_MYBATIS_SQL_TYPE.put("java.util.Date", "TIMESTAMP");
    }

    static {
        SQL_MAP_JAVA_TYPE.put("INTEGER", "java.lang.Integer");
        SQL_MAP_JAVA_TYPE.put("INT", "java.lang.Integer");
        SQL_MAP_JAVA_TYPE.put("DECIMAL", "java.math.BigDecimal");
        SQL_MAP_JAVA_TYPE.put("BIGINT", "java.lang.Long");
        SQL_MAP_JAVA_TYPE.put("SMALLINT", "java.lang.Short");
        SQL_MAP_JAVA_TYPE.put("FLOAT", "java.lang.Float");
        SQL_MAP_JAVA_TYPE.put("DOUBLE", "java.lang.Double");
        SQL_MAP_JAVA_TYPE.put("CHAR", "java.lang.Character");
        SQL_MAP_JAVA_TYPE.put("VARCHAR", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("LONGTEXT", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("TEXT", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("ENUM", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("BIT", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("TINYINT", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("JSON", "java.lang.String");
        SQL_MAP_JAVA_TYPE.put("BLOB", "[Ljava.lang.Byte");
        SQL_MAP_JAVA_TYPE.put("VARBINARY", "[Ljava.lang.Byte");
        SQL_MAP_JAVA_TYPE.put("TIMESTAMP", "java.util.Date");
        SQL_MAP_JAVA_TYPE.put("DATETIME", "java.util.Date");
        SQL_MAP_JAVA_TYPE.put("DATE", "java.util.Date");
    }

    static {
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TINYINT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("SMALLINT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("MEDIUMINT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("INT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("INTEGER", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("BIGINT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("FLOAT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("DOUBLE", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("REAL", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("DECIMAL", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("NUMERIC", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("DATE", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TIME", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("DATETIME", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TIMESTAMP", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("YEAR", 4);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("CHAR", 1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("VARCHAR", 255);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("BINARY", 1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("VARBINARY", 255);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TINYBLOB", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("BLOB", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("MEDIUMBLOB", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("LONGBLOB", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TINYTEXT", 255);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("TEXT", 6553);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("MEDIUMTEXT", 6553);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("LONGTEXT", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("ENUM", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("SET", -1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("BOOLEAN", 1);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("SERIAL", 4);
        SQL_TYPE_MAP_DEFAULT_LENGTH.put("JSON", -1);
    }

    public static String getMybatisType(Field field) {
        String type = field.getType().getName();
        String mysqlType = JAVA_MAP_MYBATIS_SQL_TYPE.get(type);
        if (mysqlType == null) {
            throw new IllegalArgumentException("Unsupported field: " + field);
        }
        return mysqlType;
    }

    public static String getMybatisType(Element element) {
        String type = element.asType().toString();
        String mysqlType = JAVA_MAP_MYBATIS_SQL_TYPE.get(type);
        if (mysqlType == null) {
            throw new IllegalArgumentException("Unsupported element: " + element);
        }
        return mysqlType;
    }

    public static String getMybatisType(String javaType) {
        String mysqlType = JAVA_MAP_MYBATIS_SQL_TYPE.get(javaType);
        if (StringUtil.isEmpty(mysqlType)) {
            throw new IllegalArgumentException("Unsupported type: " + javaType);
        }
        return mysqlType;
    }

    public static String getJavaType(String sqlType) {
        String javaType = SQL_MAP_JAVA_TYPE.get(sqlType.toUpperCase());
        if (StringUtil.isEmpty(javaType)) {
            throw new IllegalArgumentException("Unsupported type: " + sqlType);
        }
        return javaType;
    }

    public static Integer getSqlTypeDefaultLength(Field field) {
        Integer length = SQL_TYPE_MAP_DEFAULT_LENGTH.get(getMybatisType(field));
        if (Objects.isNull(length)) {
            throw new IllegalArgumentException("Unsupported field: " + field);
        }
        return length;
    }

    public static Integer getSqlTypeDefaultLengthBySqlType(String sqlType) {
        Integer length = SQL_TYPE_MAP_DEFAULT_LENGTH.get(sqlType);
        if (Objects.isNull(length)) {
            throw new IllegalArgumentException("Unsupported type: " + sqlType);
        }
        return length;
    }

    public static Integer getSqlTypeDefaultLength(Element element) {
        Integer length = SQL_TYPE_MAP_DEFAULT_LENGTH.get(getMybatisType(element));
        if (Objects.isNull(length)) {
            throw new IllegalArgumentException("Unsupported element: " + element);
        }
        return length;
    }

    public static Integer getSqlTypeDefaultLength(String javaType) {
        Integer length = SQL_TYPE_MAP_DEFAULT_LENGTH.get(getMybatisType(javaType));
        if (Objects.isNull(length)) {
            throw new IllegalArgumentException("Unsupported javaType: " + javaType);
        }
        return length;
    }

}
