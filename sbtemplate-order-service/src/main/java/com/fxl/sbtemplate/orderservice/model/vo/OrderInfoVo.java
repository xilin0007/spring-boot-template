package com.fxl.sbtemplate.orderservice.model.vo;

import com.fxl.sbtemplate.orderservice.model.OrderInfo;
import lombok.ToString;

/**
 * @Description OrderInfoVo
 * @author fangxilin
 * @date 2020-08-10
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class OrderInfoVo extends OrderInfo {

    /**
     * 就诊开始至结束时间
     */
    private String beginToEndTime;
    /**
     * 费用
     */
    private Double payAmt;
    /**
     * 扩展数据
     */
    private String extendData;
    /**
     * 订单当前就诊指引状态
     */
    private String currentStaus;

    /**
     * 订单就诊指引完成状态（kafka消息队列用到）
     */
    private String accomplishStatus;

    public String getBeginToEndTime() {
        return beginToEndTime;
    }

    public void setBeginToEndTime(String beginToEndTime) {
        this.beginToEndTime = beginToEndTime;
    }

    public Double getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Double payAmt) {
        this.payAmt = payAmt;
    }

    public String getExtendData() {
        return extendData;
    }

    public void setExtendData(String extendData) {
        this.extendData = extendData;
    }

    public String getCurrentStaus() {
        return currentStaus;
    }

    public void setCurrentStaus(String currentStaus) {
        this.currentStaus = currentStaus;
    }

    public String getAccomplishStatus() {
        return accomplishStatus;
    }

    public void setAccomplishStatus(String accomplishStatus) {
        this.accomplishStatus = accomplishStatus;
    }
}
