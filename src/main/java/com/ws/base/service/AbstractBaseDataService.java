package com.ws.base.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ws.base.mapper.BaseDataMapper;
import com.ws.base.model.BaseModel;
import com.ws.excepion.IException;
import com.ws.utils.ExcelUtil;
import com.ws.utils.StringUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GSF
 * <p>BaseServiceImpl implements BaseService</p>
 */
public abstract class AbstractBaseDataService<M extends BaseDataMapper<T>, T extends BaseModel> implements BaseDataService<T> {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>获取对应mapper</p>
     *
     * @return M extends BaseMapper<T extends BaseModel>
     * @author wangshuhunyin
     **/
    public abstract M getMapper();

    /**
     * <p>保存</p>
     *
     * @param map {columnName : value}
     * @return int
     * @author wangshuhunyin
     **/
    @Transactional(rollbackFor = Exception.class)
    public int save(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            throw new IException("保存失败");
        }
        T model = null;
        try {
            model = this.getModelClazz().getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            log.error("获取实体类失败,请检查泛型", e);
        }
        if (Objects.isNull(model)) {
            throw new IException("保存失败");
        }
        model.setValuesByMap(map);
        return this.save(model);
    }

    /**
     * <p>
     * 保存
     * 如果转更新,防止更新参数和条件参数冲突,强制将所有参数添加new{列名}作为新值,并且只会保留id作为唯一更新条件
     * 当然更新还是建议老老实实使用update去更,一是即使不存在大部分情况不会造成很大损失,二是使用这个会多一次查询验证
     * 再提一下,前端调用save更新时,不需要使用new{列名}作为参数,因为save最终调用的是save(T model),你使用new{列名}作为参数在转换为实体类的时候参数会被过滤掉.总之还是老老实实调用update
     * </p>
     *
     * @param model extends BaseModel
     * @return BaseModel
     * @author wangshuhunyin
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(T model) {
        if (Objects.nonNull(model)) {
            String id = String.valueOf(model.obtainAnyValue("id"));
            if (StringUtil.isNotEmpty(id)) {
                if (Objects.nonNull(this.select(String.valueOf(model.obtainAnyValue("id"))))) {
                    Map<String, Object> param = new HashMap<>();
                    model.toMap().forEach((k, v) -> param.put(StringUtil.concat("new", StrUtil.upperFirst(k)), v));
                    param.put("id", id);
                    log.warn("该数据已存在,转更新.为了防止更新参数和条件参数冲突,参数强制修改为: {}", param);
                    return this.update(param);
                }
            }
            model = this.saveParamFilter(model);
            if (this.saveValidate(model)) {
                return this.getMapper().save(model);
            }
        }
        throw new IException("保存失败");
    }

    public T saveParamFilter(T model) {
        if (Objects.isNull(model)) {
            throw new IException("不能保存空实体类");
        }
        if (model.fieldIsExist("id")) {
            if (StringUtil.isEmpty(model.obtainAnyValue("id"))) {
                model.setAnyValue("id", this.getUUID());
            }
        }
        if (model.fieldIsExist("createdAt")) {
            if (Objects.isNull(model.obtainAnyValue("createdAt"))) {
                model.setAnyValue("createdAt", new Date());
            }
        }
        return model;
    }

    /**
     * <p>保存验证</p>
     *
     * @param model 实体类
     * @return boolean
     * @author wangshuhunyin
     **/
    public boolean saveValidate(T model) {
        log.info("保存操作参数: {}", model.toJson());
        return true;
    }

    /**
     * <p>删除</p>
     *
     * @param map {columnName : value}
     * @return BaseModel
     * @author wangshuhunyin
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Map<String, Object> map) {
        map = this.deleteParamFilter(map);
        if (this.deleteValidate(map)) {
            return this.getMapper().delete(map);
        }
        throw new IException("删除失败");
    }

    public Map<String, Object> deleteParamFilter(Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            throw new IException("删除操作参数不建议为空!如场景需要,建议单独写一个方法(也可重写该验证方法,但不建议!)");
        }
        return map;
    }

    /**
     * <p>删除验证</p>
     *
     * @param map {columnName : value}
     * @return boolean
     * @author wangshuhunyin
     **/
    public boolean deleteValidate(Map<String, Object> map) {
        log.info("删除操作参数: {}", JSON.toJSONString(map));
        return true;
    }

    /**
     * <p>删除</p>
     *
     * @param model extends BaseModel
     * @return int
     * @author wangshuhunyin
     **/
    @Transactional(rollbackFor = Exception.class)
    public int delete(@NotNull T model) {
        return this.delete(model.toMap());
    }

    /**
     * <p>删除</p>
     *
     * @param id id
     * @return int
     * @author wangshuhunyin
     **/
    @Transactional(rollbackFor = Exception.class)
    public int delete(String id) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        return this.delete(map);
    }

    /**
     * <p>更新</p>
     *
     * @param map {columnName : value}
     * @return int
     * @author wangshuhunyin
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Map<String, Object> map) {
        map = updateParamFilter(map);
        if (this.updateValidate(map)) {
            return this.getMapper().update(map);
        }
        throw new IException("更新失败");
    }

    /**
     * <p>更新参数过滤,这里的new是为了防止更新条件和更新值参数冲突,只有更新操作强制这样做(有更好方案可替换)</p>
     *
     * @param map {columnName : value}
     * @return int
     * @author wangshuhunyin
     **/
    public Map<String, Object> updateParamFilter(Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            throw new IException("更新操作参数不建议为空!如场景需要,建议单独写一个方法(也可重写该验证方法,但不建议!)");
        }
        if (StringUtil.isEmpty(map.get("newUpdatedAt"))) {
            map.put("newUpdatedAt", new Date());
        }
        map.remove("newCreatedAt");
        return map;
    }

    /**
     * <p>删除验证</p>
     *
     * @param map {columnName : value}
     * @return boolean
     * @author wangshuhunyin
     **/
    public boolean updateValidate(Map<String, Object> map) {
        log.info("更新操作参数: {}", map);
        if (map.keySet().stream().filter(key -> !key.startsWith("new")).toList().isEmpty()) {
            log.warn("更新条件参数可能没有有效条件,请检查相关代码");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(String id, String column1, Object param1) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        map.put(column1, param1);
        return this.update(map);
    }

    /**
     * <p>
     * 更新
     * 防止更新参数和条件参数冲突,会强制将实体类所有字段添加new{列名}作为新值,并且只会保留id作为唯一更新条件
     * </p>
     *
     * @param model extends BaseModel
     * @return int
     * @author wangshuhunyin
     **/
    @Transactional(rollbackFor = Exception.class)
    public int update(@NotNull T model) {
        Map<String, Object> param = new HashMap<>();
        model.toMap().forEach((k, v) -> param.put(StringUtil.concat("new", StrUtil.upperFirst(k)), v));
        param.put("id", model.obtainAnyValue("id"));
        log.warn("实体类更新.为了防止更新参数和条件参数冲突,参数强制修改为: {}", param);
        return this.update(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(Object... keyValuesArray) {
        if (Objects.isNull(keyValuesArray) || keyValuesArray.length == 0) {
            throw new IException("更新失败");
        }
        Map<String, Object> map = new HashMap<>();
        int length = keyValuesArray.length;
        for (int i = 0; i < keyValuesArray.length; i++) {
            if (i % 2 == 0 && length >= i + 1) {
                map.put(String.valueOf(keyValuesArray[i]), keyValuesArray[i + 1]);
            }
        }
        return this.update(map);
    }

    /**
     * <p>查询1条</p>
     *
     * @param map {columnName : value}
     * @return T extends BaseModel
     * @author wangshuhunyin
     **/
    @Override
    public T select(Map<String, Object> map) {
        map = this.selectParamFilter(map);
        if (this.selectValidate(map)) {
            try {
                return this.getMapper().select(map);
            } catch (MyBatisSystemException e) {
                if (Objects.nonNull(e.getCause()) && StringUtil.isNotEmpty(e.getCause().getMessage()) && e.getCause().getMessage().contains("Expected one result (or null) to be returned by selectOne(), but found: ")) {
                    throw new IException("参数异常", e);
                }
                throw new IException("查询失败");
            }
        }
        throw new IException("查询失败");
    }

    public Map<String, Object> selectParamFilter(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            throw new IException("查询操作参数不能为空!");
        }
        Map<String, Object> param = new HashMap<>();
        map.forEach((k, v) -> {
            if (StringUtil.isNotEmpty(v)) {
                param.put(k, v);
            }
        });
        return param;
    }

    public boolean selectValidate(Map<String, Object> map) {
        log.info("查询操作参数: {}", JSON.toJSONString(map));
        return true;
    }

    /**
     * <p>查询1条</p>
     *
     * @param model extends BaseModel
     * @return T extends BaseModel
     * @author wangshuhunyin
     **/
    public T select(@NotNull T model) {
        return this.select(model.toMap());
    }

    /**
     * <p>查询1条</p>
     *
     * @param id id
     * @return T extends BaseModel
     * @author wangshuhunyin
     **/
    public T select(String id) {
        if (StringUtil.isEmpty(id)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        return this.select(map);
    }

    /**
     * <p>查询1条</p>
     *
     * @param column1 columnName
     * @param param1  value
     * @param column2 columnName
     * @param param2  value
     * @return T extends BaseModel
     * @author wangshuhunyin
     **/
    public T select(String column1, Object param1, String column2, Object param2) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(column1, param1);
        map.put(column2, param2);
        return this.select(map);
    }

    public T select(Object... keyValuesArray) {
        if (keyValuesArray != null && keyValuesArray.length > 0) {
            Map<String, Object> map = new HashMap<>();
            int length = keyValuesArray.length;
            for (int i = 0; i < length; i++) {
                if (i % 2 == 0 && length > i + 1) {
                    map.put(String.valueOf(keyValuesArray[i]), keyValuesArray[i + 1]);
                }
            }
            return this.select(map);
        }
        return null;
    }

    /**
     * <p>查询列表</p>
     *
     * @param map {columnName : value}
     * @return List<T> T extends BaseModel
     * @author wangshuhunyin
     **/
    @Override
    public List<Map<String, Object>> getList(Map<String, Object> map) {
        map = this.listParamFilter(map);
        if (this.listValidate(map)) {
            return this.getMapper().getList(map);
        }
        throw new IException("参数不合法");
    }

    /**
     * <p>查询列表</p>
     *
     * @param model extends BaseModel
     * @return List<T> T extends BaseModel
     * @author wangshuhunyin
     **/
    public List<Map<String, Object>> getList(@NotNull T model) {
        return this.getList(model.toMap());
    }

    /**
     * <p>查询列表</p>
     *
     * @param column column
     * @param value  value
     * @return List<T> T extends BaseModel
     * @author wangshuhunyin
     **/
    public List<Map<String, Object>> getList(String column, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(column, value);
        return this.getList(map);
    }

    /**
     * <p>查询列表</p>
     *
     * @param keyValues 键值对,偶数长度。key, value, key, value...
     * @return List<Map < String, Object>>
     * @author wangshuhunyin
     **/
    public List<Map<String, Object>> getList(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();
        if (keyValues != null && keyValues.length > 0) {
            int length = keyValues.length;
            for (int i = 0; i < keyValues.length; i++) {
                if (i % 2 == 0 && length >= i + 1) {
                    map.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
                }
            }
            return this.getList(map);
        }
        return this.getList(map);
    }

    @Override
    public List<T> getNestList(Map<String, Object> map) {
        map = this.listParamFilter(map);
        if (this.listValidate(map)) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            List<T> nestList = this.getMapper().getNestList(map);
            stopWatch.stop();
            log.info("查询嵌套列表操作耗时: {} ms", stopWatch.getTotalTimeMillis());
            return nestList;
        }
        throw new IException("参数不合法");
    }

    public List<T> getNestList(T model) {
        return this.getNestList(model.toMap());
    }

    public List<T> getNestList(String column, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(column, value);
        return this.getNestList(map);
    }

    public List<T> getNestList(Object... keyValuesArray) {
        if (keyValuesArray != null && keyValuesArray.length > 0) {
            Map<String, Object> map = new HashMap<>();
            int length = keyValuesArray.length;
            for (int i = 0; i < keyValuesArray.length; i++) {
                if (i % 2 == 0 && length >= i + 1) {
                    map.put(String.valueOf(keyValuesArray[i]), keyValuesArray[i + 1]);
                }
            }
            return this.getNestList(map);
        }
        return List.of();
    }

    public Map<String, Object> listParamFilter(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        int pageIndex;
        try {
            pageIndex = Integer.parseInt(String.valueOf(map.get("pageIndex")));
        } catch (Exception e) {
            pageIndex = 1;
        }
        if (StringUtil.isEmpty(map.get("pageIndex")) || pageIndex <= 0) {
            pageIndex = 1;
        }
        int pageSize;
        try {
            pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        } catch (Exception e) {
            pageSize = 10;
        }
        if (StringUtil.isEmpty(pageSize) || pageSize <= 0) {
            pageSize = 10;
        }
        map.put("pageIndex", (pageIndex - 1) * pageSize);
        map.put("pageSize", pageSize);
        Map<String, Object> param = new HashMap<>();
        map.forEach((k, v) -> {
            if (StringUtil.isNotEmpty(v)) {
                param.put(k, v);
            }
        });
        return param;
    }

    public boolean listValidate(Map<String, Object> map) {
        log.info("查询列表操作参数: {}", JSON.toJSONString(map));
        return true;
    }

    @Override
    public int getTotal(Map<String, Object> map) {
        return this.getMapper().getTotal(map);
    }

    public int getTotal() {
        return this.getMapper().getTotal(Map.of());
    }

    public void exportExcel(String fileName, List<Map<String, Object>> data, HttpServletResponse response) {
        if (StringUtil.isEmpty(fileName)) {
            throw new IException("请指定文件名");
        }
        if (Objects.isNull(data) || Objects.isNull(data.get(0))) {
            throw new IException("暂无数据");
        }
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            if (!fileName.contains(".xlsx") && !fileName.contains(".xls")) {
                fileName += ".xlsx";
            }
            ArrayList<String> dataKeyList = new ArrayList<>(data.get(0).keySet());
            ExcelUtil.writeOneSheetExcel(data, dataKeyList, dataKeyList, null, null, fileName, response);
        } catch (IOException e) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            throw new IException("Excel导出失败", e);
        }
    }

    public List<Field> getClazzFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public Class<T> getModelClazz() {
        Type type = this.getClass().getGenericSuperclass();
        while (!(type instanceof ParameterizedType)) {
            type = ((Class<?>) type).getGenericSuperclass();
        }
        List<Type> actualTypeArguments = List.of(((ParameterizedType) type).getActualTypeArguments());
        if (actualTypeArguments.size() < 2) {
            return null;
        }
        return (Class<T>) actualTypeArguments.get(1);
    }

    public Class<M> getMapperClazz() {
        Type type = this.getClass().getGenericSuperclass();
        while (!(type instanceof ParameterizedType)) {
            type = ((Class<?>) type).getGenericSuperclass();
        }
        List<Type> actualTypeArguments = List.of(((ParameterizedType) type).getActualTypeArguments());
        if (actualTypeArguments.size() < 2) {
            return null;
        }
        return (Class<M>) actualTypeArguments.get(0);
    }

    public List<Field> getModelFields() {
        return this.getClazzFields(this.getModelClazz());
    }

    public Map<String, Field> getModelMapFields() {
        return this.getModelFields().stream().collect(Collectors.toMap(Field::getName, value -> value));
    }

}

