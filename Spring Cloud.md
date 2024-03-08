## 一、Spring Cloud 各组件

### 服务注册与发现

旧：Eureka

新：Consul(JAVA)、Etcd(GO)、Nacod(Alibaba)

### 服务负载与调用

旧：NETFLIX OSS RIBBON、NETFLIX FEIGN

新：OpenFeign、LoadBalancer

### 分布式事务

Seata(主流)(Alibaba)、LCN、Hmily

### 服务熔断降级

旧：HYSTRIX

新：Circuit Breaker(Resilience4J)、Sentinel(Alibaba)

### 服务链路追踪

旧：Sleuth+Zipkin

新：Micrometer Tracing

### 服务网关

旧：NETFLIX Zuul

新：Gateway

### 服务分布式配置

旧：Spring Cloud Config+Bus

新：Consul、Nacod(Alibaba)

### 服务开发

Spring Boot

## 二、各组件功能

### 1.服务注册与发现

consul：一套开源的分布式服务发现和配置管理系统

- 服务发现：实例可以向 Consul 代理注册，客户端可以使用 Spring 管理的 bean 发现实例
- 支持 Spring Cloud LoadBalancer - Spring Cloud 项目提供的客户端负载均衡器
- 通过 Spring Cloud Gateway 支持 API 网关、动态路由器和过滤器
- 分布式配置：使用Consul键/值存储
- 控制总线：使用 Consul Events 的分布式控制事件

pom.xml

```pom
        <!--SpringCloud consul discovery-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>commons-logging</groupId>
                    <artifactId>commons-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```
application.yml

```yml
spring:
  application:
    name: cloud-name
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
        prefer-ip-address: true
```
main.java

```java
@EnableDiscoveryClient
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main80.class, args);
    }
}
```

Caused by: java.net.UnknownHostException: cloud-payment-service

consul天生支持负载均衡，RestTemplate的Bean对象添加注解@LoadBalanced
