package com.zeal.rpc.server.core;

import org.junit.jupiter.api.Test;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 21:34
 * @Version 1.0
 */
class RegistryTest {
   
    @Test
    void registryTest() {
        Registry registry = new Registry("127.0.0.1:2181");
        registry.register("zeal");
    }
}