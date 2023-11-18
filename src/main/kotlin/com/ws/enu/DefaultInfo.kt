package com.ws.enu

import com.ws.exception.ErrorInfo

enum class DefaultInfo(private var resultCode: String?, private var resultMsg: String?) : ErrorInfo {

    /**
     * 定义返回信息枚举对象
     *
     * @author wangshuhunyin

     */
    SUCCESS("200", "成功!"),
    ERROR("-1", "请求失败!"),
    REFUSE("403", "拒绝连接!"),
    PERMISSION_ERROR("403", "权限不足!"),
    WARN("warn", "警告!"),
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    NOT_FOUND("404", "未找到该资源!"),
    TOKEN_ERROR("401", "身份验证失败!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503", "服务器正忙,请稍后再试!");

    override fun getResultCode(): String? {
        return resultCode
    }

    override fun getResultMsg(): String? {
        return resultMsg
    }
}
