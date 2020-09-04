package com.fxl.sbtemplate.orderservice.enums;

/**
 * @Description 挂号订单就诊指引状态
 * @author fangxilin
 * @date 2020/8/11
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020/8/11
 */
public enum OrderStatus {

    RESERVATION("reservation", "预约"),
    SIGN("sign", "签到"),
    EXAMINE_PAY("examinePay", "检验检查缴费"),
    EXAMINE("examine", "做检验检查"),
    REPORT("report", "报告单"),
    OUTPATIENT_PAY("outpatientPay", "门诊/处方单缴费"),
    MEDICINE("medicine", "取药"),
    ACCOMPLISH("accomplish", "指引已完成");


    private String value;
    private String name;

    OrderStatus(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
