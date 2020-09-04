package com.fxl.sbtemplate.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description 考虑到admin client与server域名存在不一样的情况，配置允许所有域名访问（慎重配置）
 * @author fangxilin
 * @date 2020-08-30
 * @Copyright: 深圳市宁远科技股份有限公司版权所有(C)2020
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");

    }
}
