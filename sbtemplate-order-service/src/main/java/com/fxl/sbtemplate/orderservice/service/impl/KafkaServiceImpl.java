package com.fxl.sbtemplate.orderservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.fxl.sbtemplate.orderservice.config.KafkaConfiguration;
import com.fxl.sbtemplate.orderservice.enums.OrderStatus;
import com.fxl.sbtemplate.orderservice.mapper.OrderInfoMapper;
import com.fxl.sbtemplate.orderservice.model.OrderInfo;
import com.fxl.sbtemplate.orderservice.model.vo.OrderInfoVo;
import com.fxl.sbtemplate.orderservice.service.KafkaService;
import com.fxl.sbtemplate.util.util.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Description KafkaServiceImpl
 * @author fangxilin
 * @date 2020-08-12
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    /**
     * 订单完成指引状态值
     */
    private static final byte ACP_STATUS = 1;


    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public void sendMessageSync(Message message) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(message.getTopic(), message).get(10, TimeUnit.SECONDS);
    }

    @Override
    //@Transactional(rollbackFor = RuntimeException.class) //添加kafka事物
    public void sendMessageAsync(Message message) {
        ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(message.getTopic(), message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Message>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("推送kafka消息失败", ex);
            }
            @Override
            public void onSuccess(SendResult<String, Message> result) {
                log.info("推送kafka消息成功，topic：{}，value：{}，partition：{}，offset：{}",
                        result.getRecordMetadata().topic(), JSON.toJSONString(message), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * 监听消费 更新订单指引状态 消息队列
     * @date 2020/8/13 15:42
     * @auther fangxilin
     * @param record
     * @return void
     */
    @KafkaListener(topics = KafkaConfiguration.TOPIC_ORDER_UPDATE_STATUS, containerFactory = "batchFactory")
    //@SendTo("topic-ckl") //消息转发到指定的topic
    public void listenUpdOrdStatusMQ(ConsumerRecords<String, Message> records) {
        try {
            List<OrderInfo> updList = new ArrayList<>();
            for (ConsumerRecord<String, Message> record : records) {
                Optional<Message> kafkaMessage = Optional.ofNullable(record.value());
                if (!kafkaMessage.isPresent()) {
                    continue;
                }
                Message message = kafkaMessage.get();
                log.info("接收到更新订单kafka消息，topic：{}，value：{}，partition：{}，offset：{}", record.topic(), JSON.toJSONString(message), record.partition(), record.offset());
                if (message.getData() == null) {
                    continue;
                }
                OrderInfoVo vo = JSON.parseObject(JSON.toJSONString(message.getData()), OrderInfoVo.class);
                if (StringUtils.isEmpty(vo.getAccomplishStatus()) || StringUtils.isEmpty(vo.getOrderNo())) {
                    continue;
                }
                OrderInfo obj = new OrderInfo();
                obj.setUnitId(vo.getUnitId());
                obj.setOrderNo(vo.getOrderNo());
                obj.setUserId(vo.getUserId());
                obj.setMemberId(vo.getMemberId());
                String acpStatus = vo.getAccomplishStatus();
                if (OrderStatus.RESERVATION.getValue().equals(acpStatus)) {
                    obj.setReservationStatus(ACP_STATUS);
                } else if (OrderStatus.SIGN.getValue().equals(acpStatus)) {
                    obj.setSignStatus(ACP_STATUS);
                } else if (OrderStatus.EXAMINE_PAY.getValue().equals(acpStatus)) {
                    obj.setExaminePayStatus(ACP_STATUS);
                } else if (OrderStatus.EXAMINE.getValue().equals(acpStatus)) {
                    obj.setExamineStatus(ACP_STATUS);
                } else if (OrderStatus.REPORT.getValue().equals(acpStatus)) {
                    obj.setReportStatus(ACP_STATUS);
                } else if (OrderStatus.OUTPATIENT_PAY.getValue().equals(acpStatus)) {
                    obj.setOutpatientPayStatus(ACP_STATUS);
                } else if (OrderStatus.MEDICINE.getValue().equals(acpStatus)) {
                    obj.setMedicineStatus(ACP_STATUS);
                }
                updList.add(obj);
            }
            if (updList.size() > 0) {
                orderInfoMapper.updateStatusBatch(updList);
            }
        } catch (Exception e) {
            log.error("消费者-更新订单指引状态异常", e);
        }

    }








    /**
     * 监听器消费者生命周期
     */
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    /**
     * 停止消费者监听
     * @param listenerID @KafkaListener(id = "listenUpdOrdStatusMQ")
     */
    public void stop(String listenerID){
        registry.getListenerContainer(listenerID).pause();
    }
    /**
     * 继续消费者监听
     */
    public void resume(String listenerID){
        registry.getListenerContainer(listenerID).resume();
    }
    /**
     * 启动消费者监听
     */
    public void start(String listenerID){
        registry.getListenerContainer(listenerID).start();
    }

}
