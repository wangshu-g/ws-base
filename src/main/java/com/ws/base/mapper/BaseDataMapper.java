package com.ws.base.mapper;

import com.ws.base.model.BaseModel;

import java.util.List;
import java.util.Map;

/**
 * @author GSF
 * <p>BaseMapper</p>
 */
public interface BaseDataMapper<T extends BaseModel> extends BaseMapper {

    /**
     * <p>保存</p>
     *
     * @param model 保存参数
     * @return int
     * @author wangshuhunyin
     **/
    int save(T model);

    /**
     * <p>删除</p>
     *
     * @param map 删除条件
     * @return int
     * @author wangshuhunyin
     **/
    int delete(Map<String, Object> map);

    /**
     * <p>更新</p>
     *
     * @param map 更新参数
     * @return int
     * @author wangshuhunyin
     **/
    int update(Map<String, Object> map);

    /**
     * <p>查询一条</p>
     *
     * @param map 查询条件
     * @return T extends BaseModel
     * @author wangshuhunyin
     **/
    T select(Map<String, Object> map);

    /**
     * <p>查询列表</p>
     *
     * @param map 查询条件
     * @return List<Map < String, Object>>
     * @author wangshuhunyin
     **/
    List<Map<String, Object>> getList(Map<String, Object> map);

    /**
     * <p>查询列表</p>
     *
     * @param map 查询条件
     * @return List<T> T extends BaseModel
     * @author wangshuhunyin
     **/
    List<T> getNestList(Map<String, Object> map);

    int getTotal(Map<String, Object> map);

}
