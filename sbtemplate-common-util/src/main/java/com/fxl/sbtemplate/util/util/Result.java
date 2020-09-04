package com.fxl.sbtemplate.util.util;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Result<T> {

    private int returnCode;

    private String returnMsg;

    private Long timestamp;

    private T data;

    public Result(int returnCode, String returnMsg, T data) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.data = data;
    }

    public Result(int returnCode, String returnMsg, Long timestamp, T data) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.timestamp = timestamp;
        this.data = data;
    }
}
