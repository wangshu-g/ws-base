package com.ws.exception

interface ErrorInfo {

    /**
     * 获取状态码
     * @return String
     * @author wangshuhunyin

     */
    fun getResultCode(): String?

    /**
     * 获取错误描述
     * @return String
     * @author wangshuhunyin

     */
    fun getResultMsg(): String?

}
