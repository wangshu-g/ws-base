package com.ws.base.controller;

import com.ws.base.controller.delete.DeleteRestful;
import com.ws.base.controller.export.ExportExcel;
import com.ws.base.controller.list.ListRestful;
import com.ws.base.controller.nestlist.NestListRestful;
import com.ws.base.controller.save.SaveRestful;
import com.ws.base.controller.select.SelectRestful;
import com.ws.base.controller.update.UpdateRestful;
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
 * <p>AbstractBaseDataControllerRestful</p>
 */
public abstract class AbstractBaseDataControllerRestful<S extends AbstractBaseDataService<? extends BaseDataMapper<T>, T>, T extends BaseModel> implements SaveRestful<S, T>, DeleteRestful<S, T>, UpdateRestful<S, T>, SelectRestful<S, T>, ListRestful<S, T>, NestListRestful<S, T>, ExportExcel<S, T> {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>兴许有重写需求</p>
     *
     * @param request 请求体
     * @return Map<String, Object> 请求体参数
     **/
    @Override
    public Map<String, Object> getRequestParams(HttpServletRequest request) throws IOException {
        Map<String, Object> map = RequestUtil.getRequestParams(request);
        log.info("请求参数: {}", map);
        return map;
    }

}
