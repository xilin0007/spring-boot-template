package com.fxl.sbtemplate.businessservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @Description OrderService
 * @author fangxilin
 * @date 2020-08-15
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@FeignClient(value = "sbtemplate-order")
public interface OrderService {

    @RequestMapping(value = "/order/pushUpdateStatusMQ", method = RequestMethod.POST)
    public Object pushUpdateStatusMQ(@RequestBody Map<String, Object> params);

}
