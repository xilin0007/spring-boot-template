package com.fxl.sbtemplate.util.enums;

public enum ResultCode {

    SUCCESS(0, "请求成功"),
    FAIL(1, "请求失败"),
    PARAM_ERROR(40001,"参数错误"),
    PARAM_MISSING(40002, "参数不完整");

    private int returnCode;

    private String returnMsg;

    ResultCode(int returnCode, String returnMsg) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
