package com.wuming.cloud.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wuming
 * @DateTime: 2024/3/11 15:38
 **/
@Configuration
public class FeignConfig {

    @Bean
    public Retryer myRetryer(){
        // 初始间隔时间为100ms，重试间最大间隔时间为1s，最大请求次数为3(1+2)
        return new Retryer.Default(100,1,3);
    }
}
