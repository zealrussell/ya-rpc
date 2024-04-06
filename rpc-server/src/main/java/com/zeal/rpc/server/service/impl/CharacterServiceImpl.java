package com.zeal.rpc.server.service.impl;

import com.zeal.rpc.server.core.RpcService;
import com.zeal.rpc.server.service.CharacterService;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 12:00
 * @Version 1.0
 */
@RpcService(CharacterService.class)
public class CharacterServiceImpl implements CharacterService {
    @Override
    public String uppercase(String str) {
        
        return str.toUpperCase();
    }
}
