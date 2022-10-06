package com.zeal.server.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * WHAT THE ZZZZEAL
 *
 * @Author zeal
 * @Date 2022/10/5 13:21
 * @Version 1.0
 */
public class RpcBootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
