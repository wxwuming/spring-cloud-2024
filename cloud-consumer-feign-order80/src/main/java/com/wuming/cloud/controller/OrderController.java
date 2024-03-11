package com.wuming.cloud.controller;

import cn.hutool.core.date.DateUtil;
import com.wuming.cloud.apis.PayFeignApi;
import com.wuming.cloud.entities.PayDTO;
import com.wuming.cloud.resp.ResultData;
import com.wuming.cloud.resp.ReturnCodeEnum;
import jakarta.annotation.Resource;
import lombok.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author: wuming
 * @DateTime: 2024/3/7 15:36
 **/
@RestController
public class OrderController {
    @Resource
    private PayFeignApi payFeignApi;

    @PostMapping(value = "/feign/pay/add")
    public ResultData addOrder(@RequestBody PayDTO payDTO){
        System.out.println("第一步：模拟本地addOrder新增订单成功(省略sql操作)，第二步：再开启addPay支付微服务远程调用");
        return payFeignApi.addPay(payDTO);
    }

    @DeleteMapping(value = "/feign/pay/del/{id}")
    public ResultData delOrder(@PathVariable("id") Integer id){
        return payFeignApi.delPay(id);
    }

    @PutMapping(value = "/feign/pay/update")
    public ResultData updateOrder(@RequestBody PayDTO payDTO){
        return payFeignApi.updatePay(payDTO);
    }

    @GetMapping(value = "/feign/pay/get/{id}")
    public ResultData getById(@PathVariable("id") Integer id){
        System.out.println("-------支付微服务远程调用，按照id查询订单支付流水信息");
        ResultData resultData = null;
        try{
            System.out.println("调用开始-------:"+ DateUtil.now());
            resultData = payFeignApi.getById(id);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("调用异常-------:"+ DateUtil.now());
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(),e.getMessage());
        }
        return resultData;
    }

    @GetMapping(value = "/feign/pay/get/all")
    public ResultData getAll(){
        return payFeignApi.getAll();
    }

    @GetMapping(value = "/feign/pay/mylb")
    public String mylb(){
        return payFeignApi.mylb();
    }
}
