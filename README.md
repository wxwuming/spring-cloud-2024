

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

### 1.Consul

服务注册与发现、服务分布式配置中心

一套开源的分布式服务发现和配置管理系统 既能完成服务注册，更能完成分布式配置中心

- 服务发现：实例可以向 Consul 代理注册，客户端可以使用 Spring 管理的 bean 发现实例
- 支持 Spring Cloud LoadBalancer - Spring Cloud 项目提供的客户端负载均衡器
- 通过 Spring Cloud Gateway 支持 API 网关、动态路由器和过滤器
- 分布式配置：使用Consul K/V存储
- 控制总线：使用 Consul Events 的分布式控制事件

pom.xml

```pom
        <!--SpringCloud consul config-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
        
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
bootstrap.yml

```yml
spring:
  application:
    name: cloud-payment-service

  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
      config:
        profile-separator: '-'    # 文件分隔符 application-dev.yml
        format: YAML
```
main.java

```java
@EnableDiscoveryClient
@RefreshScope		
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main80.class, args);
    }
}
```

Caused by: java.net.UnknownHostException: cloud-payment-service

consul天生支持负载均衡，一旦引入微服务名字后，服务名称调用默认后面是多个，后面的负载均衡和轮询，需要在RestTemplate的Bean对象添加注解@LoadBalanced

#### CAP理论

Consistency（一致性）：所有节点在同一时间看到的数据是一致的（同步函数同步数据）		注意：保证一致性会影响吞吐量，在修改同步数据时，其他操作进行阻塞

Availability（可用性）：在任何时候读写都必须成功（异步函数同步数据）		注意：保证可用性会导致数据不一致

Partition Tolerance（分区容错性）：一个节点挂掉了不影响另一个节点对外提供服务

分区容错性是分布式系统的最基本的能力

#### 分布式服务注册中心的异同

| 组件名    | 语言 | CAP  | 服务健康检查 | 对外暴露接口 | Spring Cloud集成 |
| --------- | ---- | ---- | ------------ | ------------ | ---------------- |
| Eureka    | Java | AP   | 可配支持     | HTTP         | 已集成           |
| Consul    | Go   | CP   | 支持         | HTTP/DNS     | 已集成           |
| Zookeeper | Java | CP   | 支持         | 客户端       | 已集成           |

### 2.LoadBalancer

负载均衡和服务调用

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```

RestTemplate接口调用

```
@Bean
@LoadBalanced  // 使用@LoadBalanced注解赋予RestTemplate负载均衡的能力
public RestTemplate restTemplate(){
	return new RestTemplate();
}
```

轮询：RoundRobinLoadBalancer

随机：RandomLoadBalancer

实现接口ReactorServiceInstanceLoadBalancer自定义负载均衡算法

### 3.openFeign

@FeignClient注解接口

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MainOpenFeign80 {
    public static void main(String[] args) {
        SpringApplication.run(MainOpenFeign80.class, args);
    }
}
```

```java
@FeignClient(value = "cloud-payment-service")
public interface PayFeignApi {
}
```

```java
@RestController
public class OrderController {
    @Resource
    private PayFeignApi payFeignApi;
}
```

#### 超时机制

：默认超时时间60s

```xml
spring:
  application:
    name: cloud-consumer-openfeign-order
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: ${spring.application.name}
        prefer-ip-address: true

    openfeign:
      client:
        config:
          default:
            #连接超时时间
            connectTimeout: 3000
            #读取超时时间
            readTimeout: 3000
          cloud-payment-service:
            #连接超时时间
            connectTimeout: 5000
            #读取超时时间
            readTimeout: 5000
```

#### 重试机制

：Retryer对象

```java
@Configuration
public class FeignConfig {

    @Bean
    public Retryer myRetryer(){
        // 初始间隔时间为100ms，重试间最大间隔时间为1s，最大请求次数为3(1+2)
        return new Retryer.Default(100,1,3);
    }
}

```

#### 默认HTTPClient修改

```xml
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.3</version>
        </dependency>
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-hc5</artifactId>
            <version>13.1</version>
        </dependency>
```



```yml
spring:
  cloud:
    openfeign:
      httpclient:
        hc5:
          enabled: true
```

#### 请求响应压缩功能

```yml

spring:
  cloud:
    openfeign:
      compression:
        request:
          enabled: true
          min-request-size: 2048 #最小触发压缩的大小
          mime-types: text/xml,application/xml,application/json #触发压缩数据类型
        response:
          enabled: true
```

#### 日志打印功能

```java
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
```
```yml
logging:
  level:
    com:
      wuming:
        cloud:
          apis:
            PayFeignApi: debug
```

### 4.CircuitBreaker

#### 简介

一套规范和接口

Resilience4j = Resilience Four Java

落地的实现者是Resilience4j

#### 服务熔断



#### 服务降级

#### 服务限流

#### 服务限时

#### 服务预热

#### 接近实时的监控

#### 兜底的处理动作
