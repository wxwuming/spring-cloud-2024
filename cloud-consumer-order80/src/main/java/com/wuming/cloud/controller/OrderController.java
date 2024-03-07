package com.wuming.cloud.controller;

import com.wuming.cloud.entities.PayDTO;
import com.wuming.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: wuming
 * @DateTime: 2024/3/7 15:36
 **/
@RestController
public class OrderController {
    public static final String PaymentSrv_URL = "http://localhost:8001";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/consumer/pay/add")
    public ResultData addOrder(PayDTO payDTO) {
        return restTemplate.postForObject(PaymentSrv_URL + "/pay/add", payDTO, ResultData.class);
    }

    @GetMapping(value = "/consumer/pay/del/{id}")
    public ResultData delOrder(@PathVariable("id") Integer id) {
        restTemplate.delete(PaymentSrv_URL + "/pay/del/" + id, id);
        return ResultData.success(200);
    }

    @GetMapping(value = "/consumer/pay/update")
    public ResultData updateOrder(PayDTO payDTO) {
        restTemplate.put(PaymentSrv_URL + "/pay/update", payDTO);
        return ResultData.success(200);
    }

    @GetMapping(value = "/consumer/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id) {
        return restTemplate.getForObject(PaymentSrv_URL + "/pay/get/" + id, ResultData.class, id);
    }

    @GetMapping(value = "/consumer/pay/get/all")
    public ResultData getAll() {
        return restTemplate.getForObject(PaymentSrv_URL + "/pay/get/all", ResultData.class);
    }
}
