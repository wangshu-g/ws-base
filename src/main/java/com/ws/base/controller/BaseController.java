package com.ws.base.controller;

import com.ws.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author GSF
 * <p>BaseController</p>
 */
public interface BaseController {

    default Map<String, Object> getRequestParams(HttpServletRequest request) throws IOException {
        return RequestUtil.getRequestParams(request);
    }

    default String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
