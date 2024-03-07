package com.wuming.cloud.controller;

import com.wuming.cloud.entities.Pay;
import com.wuming.cloud.entities.PayDTO;
import com.wuming.cloud.resp.ResultData;
import com.wuming.cloud.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: wuming
 * @DateTime: 2024/3/7 10:48
 **/
@RestController
@Slf4j
@Tag(name = "支付微服务模块", description = "支付CRUD")
public class PayController {
    @Resource
    private PayService payService;

    @PostMapping(value = "/pay/add")
    @Operation(summary = "新增", description = "新增支付流水方法,json串做参数")
    public ResultData<String> addPay(@RequestBody Pay pay) {
        log.info(pay.toString());
        Integer i = payService.add(pay);
        return ResultData.success("成功插入记录，返回值:" + i);
    }

    @DeleteMapping(value = "/pay/del/{id}")
    @Operation(summary = "删除", description = "删除支付流水方法")
    public ResultData<Integer> deletePay(@PathVariable("id") Integer id) {
        log.info(id.toString());
        return ResultData.success(payService.delete(id));
    }

    @PutMapping(value = "/pay/update")
    @Operation(summary = "修改", description = "修改支付流水方法")
    public ResultData<String> updatePay(@RequestBody PayDTO payDTO) {
        Pay pay = new Pay();
        BeanUtils.copyProperties(payDTO, pay);
        Integer i = payService.update(pay);
        return ResultData.success("成功更新记录，返回值:" + i);
    }

    @GetMapping(value = "/pay/get/{id}")
    @Operation(summary = "按照ID查流水", description = "查询支付流水方法")
    public ResultData<Pay> getById(@PathVariable("id") Integer id) {
        if(id==-4)throw new RuntimeException("id异常");
        return ResultData.success(payService.getById(id));
    }

    @GetMapping(value = "/pay/get/all")
    @Operation(summary = "按照全部流水", description = "查询支付流水方法")
    public ResultData<List<Pay>> getAll() {
        return ResultData.success(payService.getAll());
    }
}
