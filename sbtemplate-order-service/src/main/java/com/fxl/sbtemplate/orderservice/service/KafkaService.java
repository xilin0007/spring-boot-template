package com.fxl.sbtemplate.orderservice.service;


import com.fxl.sbtemplate.util.util.Message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @Description KafkaService
 * @author fangxilin
 * @date 2020-08-12
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
public interface KafkaService {

    /**
     * 同步方式发送数据
     * @date 2020/8/12 19:03
     * @auther fangxilin
     * @param message
     * @return void
     */
    void sendMessageSync(Message message) throws InterruptedException, ExecutionException, TimeoutException;

    /***
     * 异步方式发送数据
     * @date 2020/8/12 19:04
     * @auther fangxilin
     * @param message
     * @return void
     */
    void sendMessageAsync(Message message);
}
