package com.zeal.rpc.server.service.impl;

import com.zeal.rpc.server.core.RpcService;
import com.zeal.rpc.server.service.CalculatorService;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 11:54
 * @Version 1.0
 */
@RpcService(CalculatorService.class)
public class CalculatorServiceImpl implements CalculatorService {
    @Override
    public float sum(float a, float b) {
        return a + b;
    }
}
