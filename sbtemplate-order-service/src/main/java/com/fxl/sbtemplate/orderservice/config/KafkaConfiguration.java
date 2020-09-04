package com.fxl.sbtemplate.orderservice.config;

import com.fxl.sbtemplate.util.util.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description kafka 配置类
 * @author fangxilin
 * @date 2020-08-12
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    /**
     * 可通过@Value此方式注入yml配置文件的值到变量中
     */
    /*@Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;*/

    /**
     * 更新订单指引状态 topic名
     */
    public static final String TOPIC_ORDER_UPDATE_STATUS = "ORDER_UPDATE_STATUS";


    /**
     * 初始化 更新订单指引状态 topic，设置分区数为2，副本数量为1(副本数数目设置要小于Broker数量)
     */
    @Bean
    public NewTopic initUpdOrdStatusTopic() {
        return new NewTopic(TOPIC_ORDER_UPDATE_STATUS, 2, (short) 1);
    }

    /**
     * 设置批量消费
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties()));
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        //factory.setConcurrency(2);
        factory.setBatchListener(true);
        //消息过滤器，过滤配置
        factory.setRecordFilterStrategy(r -> r.value().contains("fuck"));
        return factory;
    }


    /*************************加上注解@EnableKafka后，以下配置设置后会覆盖spring-boot默认注入的配置*************************************/
//
//
//    /**
//     * 自定义生产者配置
//     */
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        // 指定多个kafka集群多个地址
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094");
//        // 重试次数，0为不启用重试机制
//        props.put(ProducerConfig.RETRIES_CONFIG, 0);
//        // acks=0 把消息发送到kafka就认为发送成功
//        // acks=1 把消息发送到kafka leader分区，并且写入磁盘就认为发送成功
//        // acks=all 把消息发送到kafka leader分区，并且leader分区的副本follower对消息进行了同步就任务发送成功
//        props.put(ProducerConfig.ACKS_CONFIG, "1");
//        // 生产者空间不足时，send()被阻塞的时间，默认60s
//        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 6000);
//        // 控制批处理大小，单位为字节
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 4096);
//        // 批量发送，延迟为1毫秒，启用该功能能有效减少生产者发送消息次数，从而提高并发量
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//        // 生产者可以使用的总内存字节来缓冲等待发送到服务器的记录
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960);
//        // 消息的最大大小限制,也就是说send的消息大小不能超过这个限制, 默认1048576(1MB)
//        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576);
//        // 键的序列化方式
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        // 值的序列化方式
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        // 压缩消息，支持四种类型，分别为：none、lz4、gzip、snappy，默认为none。
//        // 消费者默认支持解压，所以压缩设置在生产者，消费者无需设置。
//        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
//        //生产消息时不添加headers，防止低版本的kafka服务端生产消息时报错No type information in headers and no default type provided
//        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
//        return props;
//    }
//    /**
//     * 设置生产者工厂
//     */
//    @Bean
//    public ProducerFactory<String, Message> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//    /**
//     * 设置生产者kafkaTemplate
//     */
//    @Bean
//    public KafkaTemplate<String, Message> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//
//
//    /**
//     * 自定义消费者配置
//     */
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> propsMap = new HashMap<>();
//        //propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        //default value type if no header，防止消费者解析时报错
//        propsMap.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Message.class);
//        return propsMap;
//    }
//
//    /**
//     * 设置消费者工厂
//     */
//    @Bean
//    public ConsumerFactory<String, Message> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(
//                consumerConfigs(),
//                new StringDeserializer(),
//                new JsonDeserializer<>(Message.class));
//    }
//    /**
//     * 设置消费者监听工厂
//     */
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Message> factory
//                = new ConcurrentKafkaListenerContainerFactory<>();
//        //设置自定义的consumerFactory()后，spring-boot默认的注入方式KafkaAutoConfiguration将失效
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }

    /*************************加上注解@EnableKafka后，以上配置设置后会覆盖spring-boot默认注入的配置*************************************/
}
