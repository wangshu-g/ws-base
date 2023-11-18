package com.ws.utils;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RequestUtil {

    public static String getClientIp(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtil.isEmpty(ip) || StringUtil.isEqual(ip, "unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtil.isEmpty(ip) || StringUtil.isEqual(ip, "unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtil.isEmpty(ip) || StringUtil.isEqual(ip, "unknown")) {
            ip = request.getRemoteAddr();
            if (StringUtil.isEqual(ip, "127.0.0.1") || StringUtil.isEqual(ip, "0:0:0:0:0:0:0:1")) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                    ip = inetAddress.getHostAddress();
                } catch (Exception e) {
                    log.error("获取IP失败", e);
                    ip = null;
                }
            }
        }
        if (StringUtil.isNotEmpty(ip) && ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    public static Map<String, Object> getRequestParams(HttpServletRequest request) throws IOException {
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEqual(request.getMethod(), RequestMethod.POST.name())) {
            map = JSON.parseObject(IoUtil.readUtf8(request.getInputStream()), new TypeReference<HashMap<String, Object>>() {
            });
        } else if (StringUtil.isEqual(request.getMethod(), RequestMethod.GET.name())) {
            String queryString = request.getQueryString();
            if (StringUtil.isNotEmpty(queryString)) {
                queryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8);
                String[] params = queryString.split("&");
                for (String param : params) {
                    if (StringUtil.isNotEmpty(param)) {
                        String[] keyVal = param.split("=");
                        if (keyVal.length == 2) {
                            map.put(keyVal[0], keyVal[1]);
                        }
                    }
                }
            }
        }
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        return map;
    }

}
