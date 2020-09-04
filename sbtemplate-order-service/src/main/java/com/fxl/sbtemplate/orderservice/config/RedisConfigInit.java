package com.fxl.sbtemplate.orderservice.config;

import com.fxl.sbtemplate.util.util.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description redis配置初始化
 * @author fangxilin
 * @date 2020-08-25
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Slf4j
@Component
public class RedisConfigInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化redis配置...");
        String location = "classpath:/redis-config.yml";
        try {
            Resource resource = new DefaultResourceLoader().getResource(location);
            List<PropertySource<?>> load = new YamlPropertySourceLoader().load(resource.getFilename(), resource);
            PropertySource<?> propertySource = load.get(0);
            Map<String, Object> ymlConfig = (Map<String, Object>) propertySource.getSource();
            log.info("------------------------------------" + propertySource.getProperty("redis.address"));
            log.info("------------------------------------" + ymlConfig.get("redis.address"));
            //初始化Redis
            //RedisManager.initSentinelPool(ymlConfig);
/*
            String name = propertySource.getName(); //name为resource.getFilename() redis-config.yml
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            properties.getProperty("redis"); //因为是properties的获取方式，所以获取到的是空
            properties.getProperty("address"); //因为是properties的获取方式，所以不要用redis.address来获取
*/
        } catch (Exception e) {
            log.error("初始化redis配置异常！！！", e);
        }
        log.info("初始化redis配置完成");
    }
}
