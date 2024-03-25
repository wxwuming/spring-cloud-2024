package com.wuming.cloud.apis;

import com.wuming.cloud.entities.PayDTO;
import com.wuming.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wuming
 * @DateTime: 2024/3/11 14:08
 **/
@FeignClient(value = "cloud-payment-service")
public interface PayFeignApi {
    /**
     * 通过feign调用支付微服务的添加方法
     * @param payDTO
     * @return
     */
    @PostMapping(value = "/pay/add")
    public ResultData addPay(@RequestBody PayDTO payDTO);

    /**
     * 通过feign调用支付微服务的删除方法
     * @param id
     * @return
     */
    @DeleteMapping(value = "/pay/del/{id}")
    public ResultData delPay(@PathVariable("id") Integer id);

    /**
     * 通过feign调用支付微服务的修改方法
     * @param payDTO
     * @return
     */
    @PutMapping(value = "/pay/update")
    public ResultData updatePay(@RequestBody PayDTO payDTO);

    /**
     * 通过feign调用支付微服务的查询方法
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/get/{id}")
    public ResultData getById(@PathVariable("id") Integer id);

    /**
     * 通过feign调用支付微服务的查询全部方法
     * @return
     */
    @GetMapping(value = "/pay/get/all")
    public ResultData getAll();


    @GetMapping(value = "/pay/get/info")
    public String mylb();

    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);
}
