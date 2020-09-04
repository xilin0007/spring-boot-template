package com.fxl.sbtemplate.orderservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.fxl.sbtemplate.orderservice.enums.OrderStatus;
import com.fxl.sbtemplate.orderservice.mapper.OrderInfoMapper;
import com.fxl.sbtemplate.orderservice.model.OrderInfo;
import com.fxl.sbtemplate.orderservice.model.vo.OrderInfoVo;
import com.fxl.sbtemplate.orderservice.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 订单serviceImpl
 * @author fangxilin
 * @date 2020-08-06
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    /**
     * 订单完成指引状态值
     */
    private static final byte ACP_STATUS = 1;

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @Override
    public OrderInfoVo getOrderStatus(String orderNo) {
        OrderInfo order = orderInfoMapper.getOrderStatus(orderNo);
        //默认当前状态为预约状态
        String currentStaus = OrderStatus.RESERVATION.getValue();
        if (order.getReservationStatus() == ACP_STATUS) {
            //状态为1已完成的话，状态值指向下一个状态
            currentStaus = OrderStatus.SIGN.getValue();
        }
        if (order.getSignStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.EXAMINE_PAY.getValue();
        }
        if (order.getExaminePayStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.EXAMINE.getValue();
        }
        if (order.getExamineStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.REPORT.getValue();
        }
        if (order.getReportStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.OUTPATIENT_PAY.getValue();
        }
        if (order.getOutpatientPayStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.MEDICINE.getValue();
        }
        if (order.getMedicineStatus() == ACP_STATUS) {
            currentStaus = OrderStatus.ACCOMPLISH.getValue();
        }
        OrderInfoVo vo = new OrderInfoVo();
        BeanUtils.copyProperties(order, vo);
        vo.setCurrentStaus(currentStaus);
        log.info("查询订单就诊指引状态，入参orderNo：{}，出参：{}", orderNo, JSON.toJSONString(vo));
        return vo;
    }

}
