package com.ws.base.controller;

import com.alibaba.fastjson.JSON;
import com.ws.base.controller.delete.Delete;
import com.ws.base.controller.export.ExportExcel;
import com.ws.base.controller.list.List;
import com.ws.base.controller.nestlist.NestList;
import com.ws.base.controller.save.Save;
import com.ws.base.controller.select.Select;
import com.ws.base.controller.update.Update;
import com.ws.base.mapper.BaseDataMapper;
import com.ws.base.model.BaseModel;
import com.ws.base.service.AbstractBaseDataService;
import com.ws.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author GSF
 * <p>BaseControllerImpl</p>
 */
public abstract class AbstractBaseDataController<S extends AbstractBaseDataService<? extends BaseDataMapper<T>, T>, T extends BaseModel> implements Save<S, T>, Delete<S, T>, Update<S, T>, Select<S, T>, List<S, T>, NestList<S, T>, ExportExcel<S, T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>兴许有重写需求</p>
     *
     * @param request 请求体
     * @return Map<String, Object> 请求体参数
     **/
    @Override
    public Map<String, Object> getRequestParams(HttpServletRequest request) throws IOException {
        Map<String, Object> map = RequestUtil.getRequestParams(request);
        log.info("请求参数: {}", JSON.toJSONString(map));
        return map;
    }

}
