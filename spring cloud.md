

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

负载均衡喝远程接口调用

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

### 4.CircuitBreaker(Resilience4j)

服务熔断、服务限流、服务限时

#### 简介

一套规范和接口

Resilience4j = Resilience Four Java

落地的实现者是Resilience4j

服务熔断、服务限流、服务限时

闭合、断开、半开

```yml
spring:
  cloud:
    openfeign:
      circuitbreaker:
        enabled: true
        group:
          enabled: true #没开分组永远不用分组的配置。精确优先、分组次之(开了分组)、默认最后
          
# Resilience4j CircuitBreaker 按照次数：COUNT_BASED 的例子
#  6次访问中当执行方法的失败率达到50%时CircuitBreaker将进入开启OPEN状态(保险丝跳闸断电)拒绝所有请求。
#  等待5秒后，CircuitBreaker 将自动从开启OPEN状态过渡到半开HALF_OPEN状态，允许一些请求通过以测试服务是否恢复正常。
#  如还是异常CircuitBreaker 将重新进入开启OPEN状态；如正常将进入关闭CLOSE闭合状态恢复正常处理请求。
resilience4j:
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分⽐CircuitBreaker变为OPEN状态。
        slidingWindowType: COUNT_BASED # 滑动窗口的类型
        slidingWindowSize: 6 #滑动窗⼝的⼤⼩配置COUNT_BASED表示6个请求，配置TIME_BASED表示6秒
        minimumNumberOfCalls: 6 #断路器计算失败率或慢调用率之前所需的最小样本(每个滑动窗口周期)。如果minimumNumberOfCalls为10，则必须最少记录10个样本，然后才能计算失败率。如果只记录了9次调用，即使所有9次调用都失败，断路器也不会开启。
        automaticTransitionFromOpenToHalfOpenEnabled: true # 是否启用自动从开启状态过渡到半开状态，默认值为true。如果启用，CircuitBreaker将自动从开启状态过渡到半开状态，并允许一些请求通过以测试服务是否恢复正常
        waitDurationInOpenState: 5s #从OPEN到HALF_OPEN状态需要等待的时间
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认值为10。在半开状态下，CircuitBreaker将允许最多permittedNumberOfCallsInHalfOpenState个请求通过，如果其中有任何一个请求失败，CircuitBreaker将重新进入开启状态。
        recordExceptions:
         - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default
       
# Resilience4j CircuitBreaker 按照时间：TIME_BASED 的例子
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #神坑的位置，timelimiter 默认限制远程1s，超于1s就超时异常，配置了降级，就走降级逻辑
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分⽐CircuitBreaker变为OPEN状态。
        slowCallDurationThreshold: 2s #慢调用时间阈值，高于这个阈值的视为慢调用并增加慢调用比例。
        slowCallRateThreshold: 30 #慢调用百分比峰值，断路器把调用时间⼤于slowCallDurationThreshold，视为慢调用，当慢调用比例高于阈值，断路器打开，并开启服务降级
        slidingWindowType: TIME_BASED # 滑动窗口的类型
        slidingWindowSize: 2 #滑动窗口的大小配置，配置TIME_BASED表示2秒
        minimumNumberOfCalls: 2 #断路器计算失败率或慢调用率之前所需的最小样本(每个滑动窗口周期)。
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认值为10。
        waitDurationInOpenState: 5s #从OPEN到HALF_OPEN状态需要等待的时间
        recordExceptions:
          - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default
  
####resilience4j bulkhead 的例子
resilience4j:
  bulkhead:
    configs:
      default:
        maxConcurrentCalls: 2 # 隔离允许并发线程执行的最大数量
                maxWaitDuration: 1s # 当达到并发调用数量时，新的线程的阻塞时间，我只愿意等待1秒，过时不候进舱壁兜底fallback
    instances:
      cloud-payment-service:
        baseConfig: default
  timelimiter:
    configs:
      default:
        timeout-duration: 20s
        
####resilience4j bulkhead -THREADPOOL的例子
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #timelimiter默认限制远程1s，超过报错不好演示效果所以加上10秒
  thread-pool-bulkhead:
    configs:
      default:
        core-thread-pool-size: 1
        max-thread-pool-size: 1
        queue-capacity: 1
    instances:
      cloud-payment-service:
        baseConfig: default
# spring.cloud.openfeign.circuitbreaker.group.enabled 请设置为false 新启线程和原来主线程脱离

####resilience4j ratelimiter 限流的例子
resilience4j:
  ratelimiter:
    configs:
      default:
        limitForPeriod: 2 #在一次刷新周期内，允许执行的最大请求数
        limitRefreshPeriod: 1s # 限流器每隔limitRefreshPeriod刷新一次，将允许处理的最大请求数量重置为limitForPeriod
        timeout-duration: 1 # 线程等待权限的默认等待时间
    instances:
        cloud-payment-service:
          baseConfig: default
```



#### 服务降级

```java
    @GetMapping(value = "/feign/pay/circuit/{id}")
    @CircuitBreaker(name = "cloud-payment-service", fallbackMethod = "myCircuitFallback")
    public String myCircuitBreaker(@PathVariable("id") Integer id)
    {
        return payFeignApi.myCircuit(id);
    }
    //myCircuitFallback就是服务降级后的兜底处理方法
    public String myCircuitFallback(Integer id,Throwable t) {
        // 这里是容错处理逻辑，返回备用结果
        return "myCircuitFallback，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~";
    }
```

#### 舱壁隔离

##### SemaphoreBulkhead

```java
/**
 *(船的)舱壁,隔离
 * @param id
 * @return
 */
@GetMapping(value = "/feign/pay/bulkhead/{id}")
@Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadFallback",type = Bulkhead.Type.SEMAPHORE)
public String myBulkhead(@PathVariable("id") Integer id)
{
    return payFeignApi.myBulkhead(id);
}
public String myBulkheadFallback(Throwable t)
{
    return "myBulkheadFallback，隔板超出最大数量限制，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~";
}
```

##### FixedThreadPoolBulkhead

```java
/**
 * (船的)舱壁,隔离,THREADPOOL
 * @param id
 * @return
 */
@GetMapping(value = "/feign/pay/bulkhead/{id}")
@Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadPoolFallback",type = Bulkhead.Type.THREADPOOL)
public CompletableFuture<String> myBulkheadTHREADPOOL(@PathVariable("id") Integer id)
{
    System.out.println(Thread.currentThread().getName()+"\t"+"enter the method!!!");
    try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
    System.out.println(Thread.currentThread().getName()+"\t"+"exist the method!!!");

    return CompletableFuture.supplyAsync(() -> payFeignApi.myBulkhead(id) + "\t" + " Bulkhead.Type.THREADPOOL");
}
public CompletableFuture<String> myBulkheadPoolFallback(Integer id,Throwable t)
{
    return CompletableFuture.supplyAsync(() -> "Bulkhead.Type.THREADPOOL，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~");
}
```

#### 服务限流

```java
@GetMapping(value = "/feign/pay/ratelimit/{id}")
@RateLimiter(name = "cloud-payment-service",fallbackMethod = "myRatelimitFallback")
public String myBulkhead(@PathVariable("id") Integer id)
{
    return payFeignApi.myRatelimit(id);
}
public String myRatelimitFallback(Integer id,Throwable t)
{
    return "你被限流了，禁止访问/(ㄒoㄒ)/~~";
}
```

### 3.Sleuth(Micrometer)+ZipKin

分布式链路追踪
