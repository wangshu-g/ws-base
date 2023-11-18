package com.ws.base.service;

import com.ws.base.model.BaseModel;

import java.util.List;
import java.util.Map;

/**
 * @author GSF
 * <p>BaseService</p>
 */
public interface BaseDataService<T extends BaseModel> extends BaseService {

    /**
     * <p>保存</p>
     *
     * @param model 实体类
     * @author wangshuhunyin
     **/
    int save(T model);

    /**
     * <p>删除</p>
     *
     * @param map {columnName : value}
     * @author wangshuhunyin
     **/
    int delete(Map<String, Object> map);

    /**
     * <p>更新</p>
     *
     * @param map {columnName : value}
     * @author wangshuhunyin
     **/
    int update(Map<String, Object> map);

    /**
     * <p>查询1条</p>
     *
     * @param map {columnName : value}
     * @author wangshuhunyin
     **/
    T select(Map<String, Object> map);

    /**
     * <p>查询列表</p>
     *
     * @param map {columnName : value}
     * @author wangshuhunyin
     **/
    List<Map<String, Object>> getList(Map<String, Object> map);

    /**
     * <p>查询嵌套列表</p>
     *
     * @param map {columnName : value}
     * @author wangshuhunyin
     **/
    List<T> getNestList(Map<String, Object> map);

    int getTotal(Map<String, Object> map);

}
