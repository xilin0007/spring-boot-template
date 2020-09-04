package com.fxl.sbtemplate.util.util;

import com.fxl.sbtemplate.util.enums.ResultCode;

public class ResultUtil {

    /**
     * 成功
     */
    public static <T> Result<T> successRet(T data) {
        return new Result<T>(ResultCode.SUCCESS.getReturnCode(), ResultCode.SUCCESS.getReturnMsg(), System.currentTimeMillis(), data);
    }

    /**
     * 失败
     */
    public static <T> Result<T> failRet(T data) {
        return new Result<T>(ResultCode.FAIL.getReturnCode(), ResultCode.FAIL.getReturnMsg(), System.currentTimeMillis(), data);
    }
    public static<T> Result<T> failRet(int code, String msg) {
        return new Result<T>(code, msg, System.currentTimeMillis(), null);
    }

    /**
     * 失败-参数不完整
     */
    public static <T> Result<T> paramMissingRet(T data) {
        return new Result<T>(ResultCode.PARAM_MISSING.getReturnCode(), ResultCode.PARAM_MISSING.getReturnMsg(), System.currentTimeMillis(), data);
    }
}
