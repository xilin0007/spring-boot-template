package com.fxl.sbtemplate.orderservice.service.impl;

import com.fxl.sbtemplate.orderservice.JUnitTestBase;
import com.fxl.sbtemplate.orderservice.model.vo.OrderInfoVo;
import com.fxl.sbtemplate.orderservice.service.OrderInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * @Description OrderServiceTest
 * @author fangxilin
 * @date 2020-08-15
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */

@EnableAutoConfiguration
public class OrderInfoServiceImplTest extends JUnitTestBase {


    @Autowired
    private OrderInfoService orderInfoService;

    //@Ignore
    @Test
    public void getOrderStatus() {
        String orderNo = "112328067";
        OrderInfoVo vo = orderInfoService.getOrderStatus(orderNo);
        System.out.println("----------------------" + vo.getCurrentStaus());
    }

}

