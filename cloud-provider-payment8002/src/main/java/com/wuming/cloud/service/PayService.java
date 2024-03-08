package com.wuming.cloud.service;

import com.wuming.cloud.entities.Pay;

import java.util.List;

/**
 * @Author: wuming
 * @DateTime: 2024/3/7 10:33
 **/
public interface PayService {
    public int add(Pay pay);
    public int delete(Integer id);
    public int update(Pay pay);

    public Pay getById(Integer id);

    public List<Pay> getAll();
}
