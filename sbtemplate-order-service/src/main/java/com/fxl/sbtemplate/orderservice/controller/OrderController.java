package com.fxl.sbtemplate.orderservice.controller;

import com.bd.framework.util.IdUtil;
import com.fxl.sbtemplate.orderservice.config.KafkaConfiguration;
import com.fxl.sbtemplate.orderservice.model.vo.OrderInfoVo;
import com.fxl.sbtemplate.orderservice.service.KafkaService;
import com.fxl.sbtemplate.orderservice.service.OrderInfoService;
import com.fxl.sbtemplate.orderservice.util.IpUtil;
import com.fxl.sbtemplate.util.date.DateFormatUtil;
import com.fxl.sbtemplate.util.util.MapUtil;
import com.fxl.sbtemplate.util.util.Message;
import com.fxl.sbtemplate.util.util.Result;
import com.fxl.sbtemplate.util.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @author fangxilin
 * @Description 挂号订单controller
 * @date 2020-08-06
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private KafkaService kafkaService;


    /*@RequestMapping(value = "/order/getOrderList", method = RequestMethod.POST)
    public Result<List<OrderInfoVo>> getOrderList(
            @RequestParam String unitId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userId,
            @RequestParam String memberId,
            @RequestParam String clinicTime) {
        try {
            List<OrderInfoVo> data = orderService.getOrderList(unitId, orderNo, userId, memberId, clinicTime, clinicTime);
            return ResultUtil.successRet(data);
        } catch (Exception e) {
            log.error("查询挂号支付订单列表异常", e);
            return ResultUtil.failRet(new ArrayList<OrderInfoVo>());
        }
    }*/


    /**
     * 查询订单就诊指引状态
     * @date 2020/8/11 14:32
     * @auther fangxilin
     * @param params
     * @return com.pay.common.util.util.Result<com.hsmallpg.orderservice.model.vo.OrderInfoVo>
     */
    @RequestMapping(value = "/order/getOrderStatus", method = RequestMethod.POST)
    public Result<OrderInfoVo> getOrderStatus(@RequestBody Map<String, Object> params) {
        //可不用try catch，全局异常处理类GlobalExceptionHandler已处理了
//        try {
        //验证必填项参数
        if (!MapUtil.checkMapParams(params, "orderNo")) {
            return ResultUtil.paramMissingRet(new OrderInfoVo());
        }
        String unitId = (String) params.get("unitId");
        String orderNo = (String) params.get("orderNo");
        String memberId = (String) params.get("memberId");
        log.info("开始查询订单就诊指引状态");
        OrderInfoVo data = orderInfoService.getOrderStatus(orderNo);
        return ResultUtil.successRet(data);
//        } catch (Exception e) {
//            log.error("查询订单就诊指引状态异常", e);
//            return ResultUtil.failRet(new OrderInfoVo());
//        }
    }

    /**
     * 推送更新订单指引状态消息队列
     * @date 2020/8/12 10:49
     * @auther fangxilin
     * @param params
     * @return com.pay.common.util.util.Result<com.hsmallpg.orderservice.model.vo.OrderInfoVo>
     */
    @RequestMapping(value = "/order/pushUpdateStatusMQ", method = RequestMethod.POST)
    public Result<Message> pushUpdateStatusMQ(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        //验证必填项参数
        if (!MapUtil.checkMapParams(params, "unitId", "orderNo", "accomplishStatus")) {
            return ResultUtil.paramMissingRet(new Message());
        }
        String unitId = (String) params.get("unitId");
        String orderNo = (String) params.get("orderNo");
        //订单完成状态，reservation：预约，sign：签到，examinePay：检验检查缴费，examine：做检验检查，report：报告单，outpatientPay：门诊/处方单缴费，medicine：取药
        String accomplishStatus = (String) params.get("accomplishStatus");
        String userId = (String) params.get("userId");
        String memberId = (String) params.get("memberId");

        //设置消息实体
        OrderInfoVo vo = new OrderInfoVo();
        vo.setUnitId(unitId);
        vo.setOrderNo(orderNo);
        vo.setAccomplishStatus(accomplishStatus);
        vo.setUserId(userId);
        vo.setMemberId(memberId);
        String sendTime = DateFormatUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        String ip = IpUtil.getIpAddr(request); //获取IP方法比较耗时
        Message message = new Message(IdUtil.createUUID(), sendTime, KafkaConfiguration.TOPIC_ORDER_UPDATE_STATUS, ip, vo);
        kafkaService.sendMessageAsync(message);
        return ResultUtil.successRet(message);
    }


}
