package com.zeal.server.service.impl;

import com.zeal.server.service.CharacterService;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 12:00
 * @Version 1.0
 */
public class CharacterServiceImpl implements CharacterService {
    @Override
    public String uppercase(String str) {
        return str.toUpperCase();
    }
}
