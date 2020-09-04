package com.fxl.sbtemplate.businessservice.feign;

import com.alibaba.fastjson.JSON;
import com.fxl.sbtemplate.businessservice.JUnitTestBase;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Map;

/**
 * @Description OrderServiceTest
 * @author fangxilin
 * @date 2020-08-15
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@EnableAutoConfiguration
public class OrderServiceTest extends JUnitTestBase {

    @Autowired
    private OrderService orderService;

    @Test
    public void pushUpdateStatusMQ() {
        Map<String, Object> params = new HashedMap();
        params.put("unitId", "104");
        params.put("orderNo", "112328067");
        params.put("accomplishStatus", "examinePay");
        params.put("userId", "220017431");
        params.put("memberId", "50509864");
        /**
         * 此处不能使用生产者中的返回对象Result<Message>，否则会报"无法将结果反序列化成Result的错"，因为spring已经将出参封装成jackson json对象了
         * Type definition error: [simple type, class com.pay.common.util.util.Result]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.pay.common.util.util.Result` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
         */
        Object result = orderService.pushUpdateStatusMQ(params);
        System.out.println("----------------------" + JSON.toJSONString(result));
    }
}
