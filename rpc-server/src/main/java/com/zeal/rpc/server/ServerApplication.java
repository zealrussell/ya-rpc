package com.zeal.rpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * WHAT THE ZZZZEAL
 * rpc服务器启动类
 * @Author zeal
 * @Date 2022/10/5 13:21
 * @Version 1.0
 */
public class App 
{
    public static void main( String[] args )
    {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
