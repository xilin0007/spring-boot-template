package com.fxl.sbtemplate.orderservice.service;


import com.fxl.sbtemplate.orderservice.model.vo.OrderInfoVo;

import java.util.List;

/**
 * @Description 订单service
 * @author fangxilin
 * @date 2020-08-06
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
public interface OrderInfoService {

    /**
     * 查询订单就诊指引状态
     * @date 2020/8/11 17:04
     * @auther fangxilin
     * @param orderNo
     * @return com.hsmallpg.orderservice.model.vo.OrderInfoVo
     */
    OrderInfoVo getOrderStatus(String orderNo);
}
