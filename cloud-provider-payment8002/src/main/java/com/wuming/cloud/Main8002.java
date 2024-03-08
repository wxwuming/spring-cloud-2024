package com.wuming.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: wuming
 * @DateTime: ${DATE} ${TIME}
 **/
@SpringBootApplication
@MapperScan("com.wuming.cloud.mapper") // 扫描 Mapper 接口
@EnableDiscoveryClient
public class Main8002 {
    public static void main(String[] args) {
        SpringApplication.run(Main8002.class, args);
    }
}