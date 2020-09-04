package com.fxl.sbtemplate.util.util;

import java.io.Serializable;

/**
 * @Description 消息公用实体类
 * @author fangxilin
 * @date 2020-08-12
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String id;
    /**
     * 消息发送时间，格式yyyy-MM-dd HH:mm:ss.SSS
     */
    private String sendTime;
    /**
     * 消息topic名称
     */
    private String topic;
    /**
     * 发送者ip
     */
    private String ip;
    /**
     * 消息数据内容
     */
    private Object data;

    public Message(){}

    public Message(String id, String sendTime, String topic, String ip, Object data) {
        this.id = id;
        this.sendTime = sendTime;
        this.topic = topic;
        this.ip = ip;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
